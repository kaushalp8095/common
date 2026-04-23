package com.project.common.service;

import com.project.common.models.adminAddAgenciesModel;
import com.project.common.models.agencyAddClientModel;
import com.project.common.repository.jpa.agencyClientRepository;
import com.project.common.repository.mongodb.agencyClientMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {

    @Autowired
    private agencyClientRepository sqlRepo;

    @Autowired
    private agencyClientMongoRepository mongoRepo;
    
    @Autowired
    private agencyNotificationService notifService;

    @Autowired
    private AgencyService agencyService;

    // --- Performance Metrics with Agency Filtering ---
    public List<Map<String, Object>> getClientsWithPerformanceMetricsByAgency(Long agencyId) {
        try {
            // 1. Try Supabase (SQL)
            return sqlRepo.getClientsWithPerformanceMetricsByAgency(agencyId);
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fallback to MongoDB for Agency: " + agencyId);
            try {
                // 2. Optimized Mongo Fetch using Repository method instead of stream filter
                List<agencyAddClientModel> mongoClients = mongoRepo.findByAgencyId(agencyId);

                List<Map<String, Object>> fallbackData = new ArrayList<>();
                for (agencyAddClientModel client : mongoClients) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", client.getId());
                    row.put("clientName", client.getClientName());
                    row.put("agencyName", client.getAgencyName());
                    row.put("email", client.getEmail());
                    row.put("contactNumber", client.getContactNumber());
                    row.put("totalCampaigns", 0);
                    row.put("activeCampaignsCount", 0);
                    row.put("leads", 0);
                    row.put("conversionRate", 0.0);
                    row.put("location", "Backup Mode");
                    fallbackData.add(row);
                }
                return fallbackData;
            } catch (Exception mongoEx) {
                System.err.println("🔥 Critical: Both Databases Failed!");
                return new ArrayList<>();
            }
        }
    }

    // ==========================================
    // SAVE / UPDATE CLIENT (With ID Sync)
    // ==========================================
    public void saveClient(agencyAddClientModel client) {
        boolean isNewClient = (client.getId() == null); // Check karo ki naya client hai ya update ho raha hai

        // 1. SQL Save
        try {
            agencyAddClientModel savedInSql = sqlRepo.save(client);
            
            // 🔴 CRITICAL STEP: SQL ne jo naya ID banaya hai, wo object ko de do 
            // Taaki MongoDB purane record ko update kare, naya duplicate na banaye.
            client.setId(savedInSql.getId()); 
            
            System.out.println("✅ Saved to Supabase with ID: " + client.getId());
        } catch (Exception e) {
            System.err.println("❌ Supabase Save Error: Fallback active");
            // Agar SQL down hai aur naya client hai, tab hi timestamp ID do Mongo ke liye
            if (isNewClient) {
                client.setId(System.currentTimeMillis());
            }
        }

        // 2. MongoDB Sync
        try {
            mongoRepo.save(client);
            System.out.println("✅ Saved to MongoDB");
        } catch (Exception e) {
            System.err.println("❌ MongoDB Save Error: " + e.getMessage());
        }

        // 3. NOTIFICATION TRIGGER LOGIC
        try {
            if (isNewClient && client.getAgencyId() != null) {
                // Agency ID se pehle Agency ki Email nikalenge
                Optional<adminAddAgenciesModel> agencyOpt = agencyService.getAgencyById(client.getAgencyId());
                
                if (agencyOpt.isPresent()) {
                    String agencyEmail = agencyOpt.get().getEmail();
                    
                    // Notification Service ko call karenge with "GENERAL_ALERT" toggle key
                    notifService.createNotificationWithCheck(
                        agencyEmail, 
                        "SUCCESS", 
                        "New Client Added", 
                        client.getClientName() + " has been added to your system.", 
                        "GENERAL_ALERT" // Yeh key humare switch case mein check hogi
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Notification trigger fail ho gaya: " + e.getMessage());
        }
    }

    // --- Delete by ID with Agency Verification ---
    public boolean deleteByIdAndAgency(Long id, Long agencyId) {
        boolean deletedFromAny = false;
        
        // 1. SQL Delete
        try {
            Optional<agencyAddClientModel> client = sqlRepo.findById(id);
            if (client.isPresent() && client.get().getAgencyId().equals(agencyId)) {
                sqlRepo.deleteById(id);
                deletedFromAny = true;
                System.out.println("✅ Deleted from Supabase");
            }
        } catch (Exception e) {
            System.err.println("❌ Supabase Delete Error");
        }

        // 2. MongoDB Delete
        try {
            Optional<agencyAddClientModel> mClient = mongoRepo.findById(id);
            if (mClient.isPresent() && mClient.get().getAgencyId().equals(agencyId)) {
                mongoRepo.deleteById(id);
                deletedFromAny = true;
                System.out.println("✅ Deleted from MongoDB");
            }
        } catch (Exception e) {
            System.err.println("❌ MongoDB Delete Error");
        }
        
        return deletedFromAny;
    }

    // ==========================================
    // FIND BY ID (Fail-Proof Fetch)
    // ==========================================
    public Optional<agencyAddClientModel> findById(Long id) {
        try {
            // Pehle SQL check karega
            Optional<agencyAddClientModel> sqlClient = sqlRepo.findById(id);
            if (sqlClient.isPresent()) {
                return sqlClient;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Client from Mongo...");
        }

        // Fallback: Mongo
        try {
            return mongoRepo.findById(id);
        } catch (Exception e) {
            System.err.println("❌ Both Databases Error fetching client: " + e.getMessage());
            return Optional.empty();
        }
    }
}