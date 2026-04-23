package com.project.common.service; 

import com.project.common.dto.agencyNotificationDTO;
import com.project.common.models.agencyNotificationLog;
import com.project.common.models.adminAddAgenciesModel; // Import Agency Model
import com.project.common.models.agencyNotificationSettings; // Import Settings Model
import com.project.common.repository.jpa.agencyNotificationJpaRepository;
import com.project.common.repository.mongodb.agencyNotificationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class agencyNotificationService {

    @Autowired
    private agencyNotificationJpaRepository jpaRepo; 

    @Autowired
    private agencyNotificationMongoRepository mongoRepo; 

    // 🔴 GATEKEEPER: Agency ki settings check karne ke liye
    @Autowired
    private AgencyService agencyService; 

    // ==========================================
    // FETCH LOGIC (With Failover)
    // ==========================================
    public List<agencyNotificationDTO> getNotificationsForAgency(String email) {
        List<agencyNotificationLog> logs;
        try {
            // Pehle SQL se layenge
            logs = jpaRepo.findByAgencyEmailOrderByCreatedAtDesc(email);
        } catch (Exception e) {
            // SQL down hone par Mongo se layenge
            System.err.println("⚠️ Supabase Down! Fetching Notifications from Mongo...");
            logs = mongoRepo.findByAgencyEmailOrderByCreatedAtDesc(email);
        }

        return logs.stream().map(log -> {
            agencyNotificationDTO dto = new agencyNotificationDTO();
            dto.setId(log.getId());
            dto.setType(log.getType());
            dto.setTitle(log.getTitle());
            dto.setMessage(log.getMessage());
            dto.setRead(log.isRead());
            dto.setCreatedAt(log.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public long getUnreadCount(String email) {
        try {
            return jpaRepo.findByAgencyEmailOrderByCreatedAtDesc(email).stream().filter(log -> !log.isRead()).count();
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Unread Count from Mongo...");
            return mongoRepo.findByAgencyEmailOrderByCreatedAtDesc(email).stream().filter(log -> !log.isRead()).count();
        }
    }

    // ==========================================
    // UPDATE LOGIC (With Failover)
    // ==========================================
    public void markAllAsRead(String email) {
        List<agencyNotificationLog> logs;
        try {
            logs = jpaRepo.findByAgencyEmailOrderByCreatedAtDesc(email);
            logs.forEach(log -> log.setRead(true));
            jpaRepo.saveAll(logs); 
        } catch (Exception e) {
            System.err.println("❌ Supabase Error: Could not mark as read in SQL.");
            // Agar SQL down hai toh Mongo se uthao aur wahan update karo
            logs = mongoRepo.findByAgencyEmailOrderByCreatedAtDesc(email);
            logs.forEach(log -> log.setRead(true));
        }

        try {
            mongoRepo.saveAll(logs); 
        } catch (Exception e) {
            System.err.println("❌ Mongo Error: Could not mark as read in Mongo.");
        }
    }

    // Original Method (Bina check ke - Testing ke liye use hoga)
    public void createNotification(String email, String type, String title, String msg) {
        saveNotifToDB(email, type, title, msg);
    }

    // 🔴 NAYA SMART METHOD: Yeh Toggle check karega
    public void createNotificationWithCheck(String email, String type, String title, String msg, String toggleKey) {
        Optional<adminAddAgenciesModel> agencyOpt = agencyService.findByEmail(email);
        
        if (agencyOpt.isPresent()) {
            agencyNotificationSettings settings = agencyOpt.get().getNotificationSettings();
            boolean isAllowed = true; // Default ON maan lete hain
            
            if (settings != null) {
                // Toggle Key ke hisaab se check karo
                switch(toggleKey) {
                    case "CAMP_STATUS": isAllowed = settings.getInAppNotifCampStatus(); break;
                    case "NEW_MSG": isAllowed = settings.getInAppNotifNewMsg(); break;
                    case "GENERAL_ALERT": isAllowed = settings.getInAppNotifPush(); break;
                    // Kal ko koi naya toggle aaye toh yahan add kar dena
                }
            }
            
            if (!isAllowed) {
                System.out.println("🛑 Notification Blocked by User Settings: " + title);
                return; // Gatekeeper ne rok diya! (Save nahi hoga)
            }
            
            // Agar allowed hai, toh save kardo
            saveNotifToDB(email, type, title, msg);
        }
    }

    // ==========================================
    // DATABASE SAVE LOGIC (With ID Sync)
    // ==========================================
    private void saveNotifToDB(String email, String type, String title, String msg) {
        agencyNotificationLog log = new agencyNotificationLog();
        log.setAgencyEmail(email);
        log.setType(type);
        log.setTitle(title);
        log.setMessage(msg);
        
        try {
            // 1. Save in SQL
            agencyNotificationLog savedLog = jpaRepo.save(log); 
            
            // 🔴 2. Sync ID
            log.setId(savedLog.getId());
            
            try {
                // 3. Save in Mongo
                mongoRepo.save(log); 
            } catch (Exception e) {
                System.err.println("❌ Mongo Error: Notification backup failed.");
            }
        } catch (Exception e) {
            System.err.println("❌ SQL Error: Fallback to Mongo for Notification Save.");
            // 4. Failover (Agar SQL down hai)
            log.setId(System.currentTimeMillis()); // Temporary ID for Mongo backup
            mongoRepo.save(log);
        }
        
        System.out.println("🔔 Notification Triggered: " + title);
    }
}