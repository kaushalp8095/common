package com.project.common.service;

import com.project.common.models.agencyAddCampaignModel;
import com.project.common.models.agencyReportCountModel; 
import com.project.common.models.adminAddAgenciesModel; // 🔴 Import for email fetch
import com.project.common.repository.jpa.agencyReportRepository;
import com.project.common.repository.jpa.agencyReportCountRepository; 
import com.project.common.repository.mongodb.agencyReportMongoRepository;
import com.project.common.repository.mongodb.agencyReportCountMongoRepository; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired private agencyReportRepository sqlRepo;
    @Autowired private agencyReportMongoRepository mongoRepo;
    @Autowired private agencyReportCountRepository countSqlRepo;
    @Autowired private agencyReportCountMongoRepository countMongoRepo;

    // 🔴 NOTIFICATION & AGENCY SERVICE INJECT KI
    @Autowired private agencyNotificationService notifService;
    @Autowired private AgencyService agencyService;

    // ==========================================
    // 1. DATA FETCH LOGIC (With Double Fail-Proof)
    // ==========================================
    public List<agencyAddCampaignModel> getAllReportsData(Long agencyId) {
        try {
            List<agencyAddCampaignModel> data = sqlRepo.findAllCampaignsForReports(agencyId);
            System.out.println("✅ Reports fetched from Supabase for Agency: " + agencyId);
            return data;
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Reports from MongoDB Atlas...");
            try {
                // Double safety net
                return mongoRepo.findByAgencyId(agencyId);
            } catch (Exception ex) {
                System.err.println("❌ Both DBs down! Returning empty list.");
                return new ArrayList<>(); // Crash se bachane ke liye
            }
        }
    }

    // ==========================================
    // 2. INCREMENT LOGIC (With Proper Failover & Sync)
    // ==========================================
    public void incrementDownloadCount(Long agencyId) {
        agencyReportCountModel stats = null;

        // Step A: SQL se check karo, fail hone pe Mongo se check karo
        try {
            stats = countSqlRepo.findByAgencyId(agencyId);
        } catch (Exception e) {
            System.err.println("⚠️ SQL fetch error, trying Mongo...");
            try {
                stats = countMongoRepo.findByAgencyId(agencyId);
            } catch (Exception ex) {
                System.err.println("❌ Both DBs fetch error for Count.");
            }
        }

        // Step B: Logic - Naya record banao ya purane ko +1 karo
        if (stats == null) {
            stats = new agencyReportCountModel();
            stats.setAgencyId(agencyId);
            stats.setReportDownloadCount(1L); 
        } else {
            stats.setReportDownloadCount(stats.getReportDownloadCount() + 1L); 
        }

        // Step C: Database mein Save karo (SQL + Mongo)
        try {
            agencyReportCountModel savedStats = countSqlRepo.save(stats);
            
            // 🔴 NAYA FIX: SQL ki ID Mongo record me force karo taaki duplicate na bane
            stats.setId(savedStats.getId());
            
            try {
                countMongoRepo.save(stats);
                System.out.println("✅ Report count synced to SQL & Mongo");
            } catch (Exception mongoEx) {
                System.err.println("⚠️ Failed to sync count to Mongo: " + mongoEx.getMessage());
            }
        } catch (Exception sqlEx) {
            System.err.println("⚠️ Failed to save count to SQL, saving to Mongo directly: " + sqlEx.getMessage());
            // Agar SQL save fail hua (down hai), toh temporary ID ke sath Mongo me save karo
            if (stats.getId() == null) {
                stats.setId(System.currentTimeMillis());
            }
            countMongoRepo.save(stats); 
        }

        // 🔴 Step D: NOTIFICATION TRIGGER (Report Download Hone Par)
        try {
            Optional<adminAddAgenciesModel> agencyOpt = agencyService.getAgencyById(agencyId);
            if (agencyOpt.isPresent()) {
                String email = agencyOpt.get().getEmail();
                
                // Gatekeeper ko call karo (GENERAL_ALERT toggle se control hoga)
                notifService.createNotificationWithCheck(
                    email, 
                    "SUCCESS", 
                    "Report Downloaded", 
                    "Your performance report was successfully generated and downloaded.", 
                    "GENERAL_ALERT"
                );
            }
        } catch (Exception e) {
            System.err.println("❌ Notification Error: " + e.getMessage());
        }
    }
}