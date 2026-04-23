package com.project.common.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.common.dto.adminAgenciesStatsdto;
import com.project.common.models.adminAddAgenciesModel;
import com.project.common.models.agencyLoginHistory;  
import com.project.common.repository.jpa.adminAgenciesRepository;
import com.project.common.repository.jpa.agencyLoginHistoryRepository; 
import com.project.common.repository.mongodb.adminAgenciesMongoRepository;
import com.project.common.repository.mongodb.agencyLoginHistoryMongoRepository; 

@Service
public class AgencyService {

    // ==========================================
    // REPOSITORIES
    // ==========================================
    @Autowired
    private adminAgenciesRepository sqlRepo;

    @Autowired
    private adminAgenciesMongoRepository mongoRepo;

    @Autowired
    private agencyLoginHistoryRepository loginSqlRepo; 

    @Autowired
    private agencyLoginHistoryMongoRepository loginMongoRepo; 
    
    @Autowired
    private AdminNotificationService adminNotifService;

    // ==========================================
    // AGENCY PROFILE LOGIC (FIXED FOR EMAIL UPDATE)
    // ==========================================
    public void saveAgency(adminAddAgenciesModel agency, Long adminId) {
    	
    	boolean isNewAgency = (agency.getId() == null || agency.getId() == 0);
    	
        if (isNewAgency && adminId != null) {
            agency.setAdminId(adminId);
        }
    	// 1. Email ko clean karein (Safety)
        String cleanEmail = agency.getEmail().toLowerCase().trim();
        agency.setEmail(cleanEmail);

        // 🔴 2. DUPLICATE EMAIL CHECK (NAYA LOGIC)
        if (agency.getId() == null || agency.getId() == 0) {
            // Case A: Nayi Agency Ban Rahi Hai (Registration)
            if (sqlRepo.existsByEmail(cleanEmail)) {
                throw new RuntimeException("Error: Ye Email pehle se registered hai!");
            }
        } else {
            // Case B: Purani Agency Profile Update Kar Rahi Hai
            if (sqlRepo.existsByEmailAndIdNot(cleanEmail, agency.getId())) {
                throw new RuntimeException("Error: Ye Email kisi aur agency ne le rakha hai!");
            }
        }
    	
        // 1. Supabase (SQL) mein save/update
        try {
            adminAddAgenciesModel savedInSql = sqlRepo.save(agency);
            
            agency.setId(savedInSql.getId()); 
            
         // 🔴 2. TRIGGER NOTIFICATION
            if (isNewAgency && adminId != null) {
                adminNotifService.createNotificationWithCheck(
                    adminId, // 👈 Hardcoded 1L hata kar variable use kiya
                    "SUCCESS", 
                    "New Agency Registered", 
                    agency.getAgencyName() + " has joined the platform.", 
                    "NEW_REGISTRATION"
                );
            }
            
            System.out.println("✅ Database Sync: SQL Updated/Saved with ID: " + savedInSql.getId());
        } catch (Exception e) {
            System.err.println("❌ SQL Sync Error: " + e.getMessage());
        }

        // 2. MongoDB mein save/update (Overwrite with same ID)
        try {
            mongoRepo.save(agency);
            System.out.println("✅ Database Sync: MongoDB Updated/Saved");
        } catch (Exception e) {
            System.err.println("❌ MongoDB Sync Error: " + e.getMessage());
        }
    }

    // Find logic with failover
    public Optional<adminAddAgenciesModel> getAgencyById(Long id) {
        try {
            Optional<adminAddAgenciesModel> sqlData = sqlRepo.findById(id);
            if(sqlData.isPresent()) return sqlData;
            return mongoRepo.findById(id);
        } catch (Exception e) {
            return mongoRepo.findById(id);
        }
    }
    
    public Optional<adminAddAgenciesModel> findByEmail(String email) {
        if (email == null) return Optional.empty();
        
        // 🔴 Login Fix: Hamesha lowercase aur clean email se search karein
        String cleanEmail = email.toLowerCase().trim();

        try {
            Optional<adminAddAgenciesModel> sqlData = sqlRepo.findByEmail(cleanEmail);
            
            // Agar Supabase se data mil gaya, toh return karo
            if (sqlData.isPresent()) {
                return sqlData;
            }
            
            // Agar Supabase up hai par data nahi mila, toh Mongo check karo
            System.out.println("Supabase empty for " + cleanEmail + ", checking Mongo...");
            return mongoRepo.findByEmail(cleanEmail);

        } catch (Exception e) {
            // Agar Supabase Offline hai, toh backup Mongo se fetch karo
            System.err.println("Supabase Offline! Switching to Mongo Atlas for: " + cleanEmail);
            return mongoRepo.findByEmail(cleanEmail);
        }
    }
    
 // Method ko update karke adminId parameter add karein
    public List<adminAgenciesStatsdto> getAllAgenciesForTable(Long adminId) {
        try {
            // Purana method hata kar naya method call karein jo adminId leta ho
            return sqlRepo.getAgenciesByAdminIdWithStats(adminId);
        } catch (Exception e) {
            System.err.println("❌ Database Error fetching stats: " + e.getMessage());
            return List.of(); 
        }
    }

    // ==========================================
    // LOGIN HISTORY LOGIC
    // ==========================================
    
    public void saveLoginHistory(agencyLoginHistory history) {
        // 1. Supabase Save 
        try {
            loginSqlRepo.save(history);
            System.out.println("✅ Login History Saved in Supabase");
        } catch (Exception e) {
            System.err.println("❌ Supabase Error (Login History): " + e.getMessage());
        }

        // 2. MongoDB Save 
        try {
            loginMongoRepo.save(history);
            System.out.println("✅ Login History Saved in MongoDB");
        } catch (Exception e) {
            System.err.println("❌ MongoDB Error (Login History): " + e.getMessage());
        }
    }

    public List<agencyLoginHistory> getLoginHistory(String email) {
        if (email == null) return List.of();
        String cleanEmail = email.toLowerCase().trim();

        try {
            List<agencyLoginHistory> sqlData = loginSqlRepo.findTop5ByAgencyEmailOrderByLoginTimeDesc(cleanEmail);
            if (sqlData != null && !sqlData.isEmpty()) {
                return sqlData;
            }
            return loginMongoRepo.findTop5ByAgencyEmailOrderByLoginTimeDesc(cleanEmail);
        } catch (Exception e) {
            System.err.println("Supabase Offline! Fetching Login History from Mongo Atlas...");
            return loginMongoRepo.findTop5ByAgencyEmailOrderByLoginTimeDesc(cleanEmail);
        }
    }
    
 // ==========================================
    // DELETE AGENCY LOGIC
    // ==========================================
    public void deleteAgency(Long id) {
        // 1. Supabase (SQL) se delete karein
        try {
            sqlRepo.deleteById(id);
            System.out.println("✅ Database Sync: SQL Deleted ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ SQL Delete Error: " + e.getMessage());
        }

        // 2. MongoDB se delete karein
        try {
            mongoRepo.deleteById(id);
            System.out.println("✅ Database Sync: MongoDB Deleted ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ MongoDB Delete Error: " + e.getMessage());
        }
    }
}