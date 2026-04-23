package com.project.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.common.models.agencyAddCampaignModel;
import com.project.common.repository.jpa.agencyAddCampaignRepository;
import com.project.common.repository.mongodb.agencyAddCampaignMongoRepository;
import java.util.*;


@Service
public class CampaignService {

    @Autowired private agencyAddCampaignRepository sqlRepo; 
    @Autowired private agencyAddCampaignMongoRepository mongoRepo; 
    @Autowired private agencyNotificationService notifService;
    @Autowired private AgencyService agencyService;

    // ==========================================
    // 1. SAVE OR UPDATE (With Dual DB Sync)
    // ==========================================
    public agencyAddCampaignModel saveOrUpdate(agencyAddCampaignModel campaign) {
        if (campaign.getClientId() == null) {
            throw new RuntimeException("Validation Failed: Client ID is required.");
        }

        boolean isNew = (campaign.getId() == null);
        agencyAddCampaignModel savedCampaign = null;

        try {
            // Primary Save: Supabase (SQL)
            savedCampaign = sqlRepo.save(campaign);
            
            // 🔴 CRITICAL STEP: SQL se aayi ID explicitly set karna 
            // Taaki MongoDB mein duplicate record na bane
            campaign.setId(savedCampaign.getId());
            
            try {
                // Secondary Save: MongoDB (Backup with SAME ID)
                mongoRepo.save(campaign); 
            } catch (Exception e) {
                System.err.println("❌ MongoDB Sync Error: " + e.getMessage());
            }
        } catch (Exception e) {
            // Failover: Agar SQL down hai toh direct Mongo mein save hoga
            System.err.println("❌ SQL Save Error: " + e.getMessage());
            savedCampaign = mongoRepo.save(campaign);
        }

        // Trigger Notification on New Campaign
        if (isNew && savedCampaign != null && savedCampaign.getAgencyId() != null) {
            triggerCampaignNotification(
                savedCampaign.getAgencyId(), 
                "SUCCESS", 
                "New Campaign Created", 
                "Campaign '" + savedCampaign.getCampaignName() + "' is now active."
            );
        }
        return savedCampaign;
    }

    // ==========================================
    // 2. FETCH LOGIC (100% Fail-Proof)
    // ==========================================
    public Optional<agencyAddCampaignModel> getByIdSecurely(Long id, Long agencyId) {
        try {
            // Pehle SQL se check karega
            Optional<agencyAddCampaignModel> sqlCampaign = sqlRepo.findById(id);
            if (sqlCampaign.isPresent() && sqlCampaign.get().getAgencyId().equals(agencyId)) {
                return sqlCampaign;
            }
        } catch (Exception e) {
            // Agar SQL Server Down hai toh yahan aayega aur Crash nahi hoga
            System.err.println("⚠️ Supabase Down! Fetching Campaign from Mongo Atlas...");
        }

        // Failover: Mongo se fetch
        try {
            Optional<agencyAddCampaignModel> mongoCampaign = mongoRepo.findById(id);
            if (mongoCampaign.isPresent() && mongoCampaign.get().getAgencyId().equals(agencyId)) {
                return mongoCampaign;
            }
        } catch (Exception e) {
            System.err.println("❌ Both Databases Error: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<agencyAddCampaignModel> getCampaignsByAgency(Long agencyId) {
        try {
            return sqlRepo.findByAgencyId(agencyId);
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Campaigns list from Mongo Atlas...");
            return mongoRepo.findByAgencyId(agencyId);
        }
    }

    public List<agencyAddCampaignModel> getByClientIdAndAgency(Long clientId, Long agencyId) {
        try {
            // 🔴 NAYA ADD KIYA: Failover Try-Catch
            return sqlRepo.findByClientIdAndAgencyId(clientId, agencyId);
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Client Campaigns from Mongo Atlas...");
            return mongoRepo.findByClientIdAndAgencyId(clientId, agencyId);
        }
    }

    // ==========================================
    // 3. DELETE LOGIC
    // ==========================================
    public boolean deleteSecurely(Long id, Long agencyId) {
        Optional<agencyAddCampaignModel> campaign = getByIdSecurely(id, agencyId);
        if (campaign.isPresent()) {
            String campName = campaign.get().getCampaignName();
            
            // Dono jagah se independent delete try karega
            try { sqlRepo.deleteById(id); } catch (Exception e) { System.err.println("SQL Delete Error"); }
            try { mongoRepo.deleteById(id); } catch (Exception e) { System.err.println("Mongo Delete Error"); }
            
            triggerCampaignNotification(agencyId, "INFO", "Campaign Deleted", "Campaign '" + campName + "' removed.");
            return true;
        }
        return false;
    }

    // ==========================================
    // 4. NOTIFICATION HELPER
    // ==========================================
    private void triggerCampaignNotification(Long agencyId, String type, String title, String msg) {
        agencyService.getAgencyById(agencyId).ifPresent(agency -> {
            notifService.createNotificationWithCheck(agency.getEmail(), type, title, msg, "CAMP_STATUS");
        });
    }
    
  //________________ SUPER ADNIN START ______________//  
 // ==========================================
 // 5. GET ALL CAMPAIGNS FOR ADMIN (Admin Filtered)
 // ==========================================
 public List<Map<String, Object>> getAllCampaignsForAdmin(Long adminId) {
     try {
         // Primary: Supabase (Filter by adminId)
         return sqlRepo.findAllCampaignsForAdmin(adminId); 
     } catch (Exception e) {
         System.err.println("⚠️ Supabase Down! Fetching Admin Campaigns from Mongo...");
         
         // Failover: Mongo se filter karke laao
         List<agencyAddCampaignModel> mongoList = mongoRepo.findByAdminId(adminId);
         List<Map<String, Object>> fallbackResult = new ArrayList<>();
         
         for (agencyAddCampaignModel camp : mongoList) {
             Map<String, Object> map = new HashMap<>();
             map.put("id", camp.getId());
             map.put("campaignName", camp.getCampaignName());
             map.put("agencyName", "Agency ID: " + camp.getAgencyId()); 
             map.put("clientName", camp.getClientName());
             map.put("startDate", camp.getStartDate());
             map.put("endDate", camp.getEndDate());
             map.put("budget", camp.getBudget());
             map.put("status", camp.getStatus());
             fallbackResult.add(map);
         }
         return fallbackResult;
     }
 }
    
 // ==========================================
    // 6. DELETE CAMPAIGN FOR ADMIN (Dual DB)
    // ==========================================
    public boolean deleteCampaignByAdmin(Long id) {
        boolean isDeleted = false;
        
        // 1. Delete from SQL
        try {
            if (sqlRepo.existsById(id)) {
                sqlRepo.deleteById(id);
                isDeleted = true;
            }
        } catch (Exception e) {
            System.err.println("❌ SQL Admin Delete Error: " + e.getMessage());
        }

        // 2. Delete from MongoDB
        try {
            if (mongoRepo.existsById(id)) {
                mongoRepo.deleteById(id);
                isDeleted = true;
            }
        } catch (Exception e) {
            System.err.println("❌ Mongo Admin Delete Error: " + e.getMessage());
        }
        
        return isDeleted;
    }
}