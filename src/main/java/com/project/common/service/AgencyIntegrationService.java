package com.project.common.service;

import java.util.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.project.common.models.agencyIntegrationModel;
import com.project.common.repository.jpa.AgencyIntegrationJPARepository;
import com.project.common.repository.mongodb.AgencyIntegrationMongoRepository;

@Service
public class AgencyIntegrationService {

    @Autowired private AgencyIntegrationJPARepository jpaRepo;
    @Autowired private AgencyIntegrationMongoRepository mongoRepo;
    @Autowired private agencyNotificationService notifService;

    @Value("${google.ads.client-id}") private String googleClientId;
    @Value("${google.ads.client-secret}") private String googleClientSecret;
    @Value("${google.ads.callback-url}") private String googleRedirectUri;

    @Value("${facebook.app-id}") private String fbAppId;
    @Value("${facebook.app-secret}") private String fbAppSecret;
    @Value("${facebook.callback-url}") private String fbRedirectUri;
    
    @Value("${google.ads.developer-token}") private String googleDeveloperToken;
    @Value("${google.ads.customer-id}") private String googleCustomerId;

    private final RestTemplate restTemplate = new RestTemplate();

    // ================= GOOGLE TOKEN EXCHANGE =================
    public void processGoogleCallback(String code, String email) {
        String url = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        try {
            Map<String, Object> response = restTemplate.postForObject(url, params, Map.class);
            if (response != null && response.containsKey("refresh_token")) {
                updateDatabase(email, (String) response.get("refresh_token"), null, "Google Ads");
            }
        } catch (Exception e) { System.err.println("Google Token Error: " + e.getMessage()); }
    }

    // ================= FACEBOOK TOKEN EXCHANGE =================
    public void processFacebookCallback(String code, String email) {
        String url = "https://graph.facebook.com/v25.0/oauth/access_token?" +
                     "client_id=" + fbAppId + "&redirect_uri=" + fbRedirectUri +
                     "&client_secret=" + fbAppSecret + "&code=" + code;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("access_token")) {
                updateDatabase(email, null, (String) response.get("access_token"), "Facebook");
            }
        } catch (Exception e) { System.err.println("FB Token Error: " + e.getMessage()); }
    }

    // ================= DUAL DATABASE SAVE LOGIC =================
    private void updateDatabase(String email, String googleRefToken, String fbAccToken, String platform) {
        agencyIntegrationModel model = null;
        try {
            model = jpaRepo.findById(email).orElse(new agencyIntegrationModel());
        } catch (Exception e) {
            try { model = mongoRepo.findById(email).orElse(new agencyIntegrationModel()); } 
            catch (Exception ex) { model = new agencyIntegrationModel(); }
        }

        model.setAgencyEmail(email);
        if (platform.equals("Google Ads")) {
            model.setGoogleRefreshToken(googleRefToken);
            model.setGoogleConnected(true);
        } else if (platform.equals("Facebook")) {
            model.setFbAccessToken(fbAccToken);
            model.setFbConnected(true);
        }

        try { jpaRepo.save(model); } catch (Exception e) { System.err.println("❌ SQL Save Error"); }
        try { mongoRepo.save(model); } catch (Exception e) { System.err.println("❌ Mongo Save Error"); }

        notifService.createNotificationWithCheck(email, "SUCCESS", platform + " Connected", 
            "Your " + platform + " account has been successfully linked.", "GENERAL_ALERT");
    }
    
    public agencyIntegrationModel getIntegrationData(String email) {
        try { return jpaRepo.findById(email).orElse(null); } 
        catch (Exception e) { return mongoRepo.findById(email).orElse(null); }
    }
    
    public void disconnectPlatform(String email, String platform) {
        agencyIntegrationModel model = getIntegrationData(email);
        if (model != null) {
            String platformName = "";
            if (platform.equalsIgnoreCase("GOOGLE")) {
                model.setGoogleRefreshToken(null);
                model.setGoogleConnected(false);
                platformName = "Google Ads";
            } else if (platform.equalsIgnoreCase("FB")) {
                model.setFbAccessToken(null);
                model.setFbConnected(false);
                platformName = "Facebook";
            }
            try { jpaRepo.save(model); mongoRepo.save(model); } catch (Exception e) {}
            notifService.createNotificationWithCheck(email, "WARNING", platformName + " Disconnected", 
                "Your data sync has been stopped.", "GENERAL_ALERT");
        }
    }
    
    // ================= FETCH RECENT CAMPAIGNS =================
    public List<Map<String, Object>> fetchRecentCampaigns(String email, String platform) {
        agencyIntegrationModel model = getIntegrationData(email);
        if (model == null) throw new RuntimeException("Integration data not found.");

        List<Map<String, Object>> campaignsList = new ArrayList<>();

        if ("google".equalsIgnoreCase(platform)) {
            if (!model.isGoogleConnected()) throw new RuntimeException("Google not connected.");
            try {
                String accessToken = getGoogleAccessTokenFromRefresh(model.getGoogleRefreshToken());
                String googleUrl = "https://googleads.googleapis.com/v16/customers/" + googleCustomerId + "/googleAds:search";
                
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.set("developer-token", googleDeveloperToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                String query = "SELECT campaign.name, campaign_budget.amount_micros, campaign.status, campaign.start_date, campaign.end_date FROM campaign LIMIT 10";
                Map<String, String> body = new HashMap<>();
                body.put("query", query);
                
                HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = restTemplate.exchange(googleUrl, HttpMethod.POST, entity, Map.class);
                
                campaignsList.addAll(extractRealGoogleCampaigns((Map<String, Object>) response.getBody()));
            } catch (Exception e) { throw new RuntimeException("Google Error: " + e.getMessage()); }
        } 
        else if ("meta".equalsIgnoreCase(platform)) {
            if (!model.isFbConnected()) throw new RuntimeException("Facebook not connected.");
            try {
                // 🔴 APNA ASLI FB AD ACCOUNT ID YAHAN DALEIN
                String adAccountId = "act_3285937368371077"; 
                String fbUrl = "https://graph.facebook.com/v19.0/" + adAccountId + "/campaigns?fields=name,daily_budget,status,start_time,stop_time&access_token=" + model.getFbAccessToken();
                
                Map<String, Object> fbResponse = restTemplate.getForObject(fbUrl, Map.class);
                campaignsList.addAll(extractRealFacebookCampaigns(fbResponse));
            } catch (Exception e) { throw new RuntimeException("Facebook Error: " + e.getMessage()); }
        }
        return campaignsList;
    }

    private String getGoogleAccessTokenFromRefresh(String refreshToken) {
        String url = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");
        Map<String, Object> response = restTemplate.postForObject(url, params, Map.class);
        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRealFacebookCampaigns(Map<String, Object> fbResponse) {
        List<Map<String, Object>> parsedList = new ArrayList<>();
        DateTimeFormatter fbInputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        DateTimeFormatter flatpickrFormat = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        try {
            List<Map<String, Object>> campaignsList = (List<Map<String, Object>>) fbResponse.get("data");
            if (campaignsList != null) {
                for (Map<String, Object> rawCamp : campaignsList) {
                    Map<String, Object> camp = new HashMap<>();
                    camp.put("campaignName", rawCamp.get("name"));
                    camp.put("platform", "Facebook");
                    if (rawCamp.containsKey("daily_budget")) {
                        camp.put("budget", String.valueOf(Double.parseDouble(rawCamp.get("daily_budget").toString()) / 100));
                    }
                    camp.put("status", "ACTIVE".equals(rawCamp.get("status")) ? "Active" : "Paused");
                    if (rawCamp.containsKey("start_time")) {
                        try {
                            OffsetDateTime odt = OffsetDateTime.parse(rawCamp.get("start_time").toString(), fbInputFormat);
                            camp.put("startDate", odt.format(flatpickrFormat));
                        } catch (Exception e) { camp.put("startDate", ""); }
                    }
                    camp.put("targetLocation", "");
                    parsedList.add(camp);
                }
            }
        } catch (Exception e) { System.err.println("FB Parse Error: " + e.getMessage()); }
        return parsedList;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRealGoogleCampaigns(Map<String, Object> googleResponse) {
        List<Map<String, Object>> parsedList = new ArrayList<>();
        try {
            List<Map<String, Object>> results = (List<Map<String, Object>>) googleResponse.get("results");
            if (results != null) {
                for (Map<String, Object> row : results) {
                    Map<String, Object> camp = new HashMap<>();
                    Map<String, Object> gCamp = (Map<String, Object>) row.get("campaign");
                    Map<String, Object> gBudget = (Map<String, Object>) row.get("campaignBudget");
                    
                    camp.put("campaignName", gCamp.get("name"));
                    camp.put("platform", "Google Ads");
                    camp.put("status", "ENABLED".equals(gCamp.get("status")) ? "Active" : "Paused");
                    if (gBudget != null) {
                        camp.put("budget", String.valueOf(Long.parseLong(gBudget.get("amountMicros").toString()) / 1000000.0));
                    }
                    camp.put("startDate", gCamp.getOrDefault("startDate", ""));
                    camp.put("targetLocation", "");
                    parsedList.add(camp);
                }
            }
        } catch (Exception e) { System.err.println("Google Parse Error: " + e.getMessage()); }
        return parsedList;
    }
}