package com.project.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.RequestEntity;
import java.net.URI;

@Service
public class EmailService {

    // application.properties se aayega: brevo.api.key=xkeysib-...
    @Value("${brevo.api.key}")
    private String apiKey;

    // application.properties se aayega: brevo.sender.email=your.verified@email.com
    @Value("${brevo.sender.email}")
    private String senderEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String brevoApiUrl = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Quantifyre Iris - Your 2FA Verification Code";
        String textContent = "Hello,\n\nYour 6-digit verification code is: " + otp + "\n\nPlease enter this code to enable Two-Factor Authentication.\n\nThank you!";
        sendEmailViaBrevoApi(toEmail, subject, textContent);
    }
    
    @Async
    public void sendLoginAlertEmail(String toEmail, String device, String location, String ipAddress) {
        String subject = "Security Alert: New Login to Your Account";
        String textContent = "Hello,\n\nWe noticed a new login to your Quantifyre Iris account.\n\n"
                + "Login Details:\n"
                + "- Device: " + device + "\n"
                + "- Location: " + location + "\n"
                + "- IP Address: " + ipAddress + "\n\n"
                + "If this was you, you can safely ignore this email.\n"
                + "If you do not recognize this activity, please change your password immediately.\n\n"
                + "Stay Secure,\nQuantifyre Team";
        sendEmailViaBrevoApi(toEmail, subject, textContent);
    }

    private void sendEmailViaBrevoApi(String toEmail, String subject, String textContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> emailData = new HashMap<>();
            emailData.put("sender", Map.of("name", "Quantifyre Team", "email", senderEmail));
            emailData.put("to", List.of(Map.of("email", toEmail)));
            emailData.put("subject", subject);
            emailData.put("textContent", textContent); 

            // 🔴 ULTIMATE FIX: HttpEntity ki jagah RequestEntity Builder pattern use kiya hai
            RequestEntity<Map<String, Object>> request = RequestEntity
                    .post(URI.create(brevoApiUrl))
                    .headers(headers)
                    .body(emailData);
            
            restTemplate.exchange(request, String.class);
            System.out.println("✅ Email successfully sent via API to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Error sending Email via API: " + e.getMessage());
        }
    }
}