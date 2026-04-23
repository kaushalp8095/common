package com.project.common.service;

import com.project.common.models.adminLoginHistory;
import com.project.common.models.adminLoginModel;
import com.project.common.repository.jpa.adminLoginHistoryRepository;
import com.project.common.repository.jpa.adminLoginRepository;
import com.project.common.repository.mongodb.adminLoginHistoryMongoRepository;
import com.project.common.repository.mongodb.adminLoginMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminLoginService {

    @Autowired
    private adminLoginRepository sqlRepo;

    @Autowired
    private adminLoginMongoRepository mongoRepo;
    
    @Autowired
    private adminLoginHistoryRepository adminSQLRepo;

    @Autowired
    private adminLoginHistoryMongoRepository adminMongoRepo;

    // Failover Logic: Pehle SQL check karo, fir Mongo
    public Optional<adminLoginModel> findByEmail(String email) {
        try {
            Optional<adminLoginModel> sqlData = sqlRepo.findByEmail(email);
            if (sqlData.isPresent()) {
                return sqlData;
            }
            System.out.println("Supabase empty, checking Mongo for Admin Login...");
            return mongoRepo.findByEmail(email);
        } catch (Exception e) {
            System.err.println("Supabase Offline! Fetching Admin from Mongo Atlas...");
            return mongoRepo.findByEmail(email);
        }
    }

    // Default admin save karne ke liye
    public void saveAdmin(adminLoginModel admin) {
        try { sqlRepo.save(admin); } catch (Exception e) { System.err.println("SQL Save Error"); }
        try { mongoRepo.save(admin); } catch (Exception e) { System.err.println("Mongo Save Error"); }
    }
    
 // Admin ko ID se dhundhne ke liye (Failover Logic)
    public Optional<adminLoginModel> getAdminById(Long id) {
        try {
            Optional<adminLoginModel> sqlData = sqlRepo.findById(id);
            if (sqlData.isPresent()) return sqlData;
            return mongoRepo.findById(id);
        } catch (Exception e) {
            return mongoRepo.findById(id);
        }
    }

    // Profile Details Update (Name, Phone)
    public adminLoginModel updateAdminProfile(Long id, String firstName, String lastName, String phone) throws Exception {
        adminLoginModel admin = getAdminById(id).orElseThrow(() -> new Exception("Admin not found"));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setPhoneNumber(phone);
        
        saveAdmin(admin); // Aapka dual-save method (SQL + Mongo)
        return admin;
    }

    // Logo Update
    public void updateProfileLogo(Long id, String logoUrl) throws Exception {
        adminLoginModel admin = getAdminById(id).orElseThrow(() -> new Exception("Admin not found"));
        admin.setProfileLogo(logoUrl);
        saveAdmin(admin);
    }

    // Password Update
    public boolean updatePassword(Long id, String oldPassword, String newPassword) throws Exception {
        adminLoginModel admin = getAdminById(id).orElseThrow(() -> new Exception("Admin not found"));
        
        if (!admin.getPassword().equals(oldPassword)) {
            return false; // Purana password match nahi hua
        }
        
        admin.setPassword(newPassword);
        saveAdmin(admin);
        return true;
    }
    
    public void updateLoginAlertStatus(Long adminId, boolean status) {
        adminLoginModel admin = getAdminById(adminId) // Use Failover method
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        admin.setLoginAlertsEnabled(status);
        saveAdmin(admin); // Use Dual-Save method (SQL + Mongo)
    }    
    
 // Login ke time dono DB mein save karne ka method
    public void saveAdminLoginHistory(adminLoginHistory history) {
        try {
            adminSQLRepo.save(history);   // Postgres mein save
            adminMongoRepo.save(history); // MongoDB mein backup save
        } catch (Exception e) {
            System.err.println("Error saving admin history: " + e.getMessage());
        }
    }

    // UI (Security Tab) mein dikhane ke liye fetch method
    public List<adminLoginHistory> getAdminLoginHistory(Long adminId) {
        return adminSQLRepo.findTop10ByAdminIdOrderByLoginTimeDesc(adminId);
    }
    
    
 // Notification Settings Update karne ke liye
    public void updateAdminNotificationSettings(Long id, com.project.common.models.agencyNotificationSettings newSettings) throws Exception {
        // getAdminById pehle se hi Optional return karta hai (Failover logic ke saath)
        adminLoginModel admin = getAdminById(id)
                .orElseThrow(() -> new Exception("Admin not found with ID: " + id));
        
        // Model mein settings set karo
        admin.setNotificationSettings(newSettings);
        
        // Dual Save (SQL + Mongo) jo aapne pehle se banaya hai
        saveAdmin(admin); 
    }
    
}