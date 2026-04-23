package com.project.common.service;

import com.project.common.dto.adminNotificationDTO;
import com.project.common.models.adminLoginModel;
import com.project.common.models.adminNotificationLog;
import com.project.common.models.agencyNotificationSettings;
import com.project.common.repository.jpa.adminNotificationJpaRepository;
import com.project.common.repository.mongodb.adminNotificationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminNotificationService {

    @Autowired private adminNotificationJpaRepository jpaRepo;
    @Autowired private adminNotificationMongoRepository mongoRepo;
    @Autowired private AdminLoginService adminService; // Toggle check karne ke liye

    // FETCH LOGIC (Failover)
    public List<adminNotificationDTO> getNotificationsForAdmin(Long adminId) {
        List<adminNotificationLog> logs;
        try {
            logs = jpaRepo.findByAdminIdOrderByCreatedAtDesc(adminId);
        } catch (Exception e) {
            logs = mongoRepo.findByAdminIdOrderByCreatedAtDesc(adminId);
        }

        return logs.stream().map(log -> {
            adminNotificationDTO dto = new adminNotificationDTO();
            dto.setId(log.getId());
            dto.setType(log.getType());
            dto.setTitle(log.getTitle());
            dto.setMessage(log.getMessage());
            dto.setRead(log.isRead());
            dto.setCreatedAt(log.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public long getUnreadCount(Long adminId) {
        try {
            return jpaRepo.findByAdminIdOrderByCreatedAtDesc(adminId).stream().filter(log -> !log.isRead()).count();
        } catch (Exception e) {
            return mongoRepo.findByAdminIdOrderByCreatedAtDesc(adminId).stream().filter(log -> !log.isRead()).count();
        }
    }

    // MARK AS READ
    public void markAllAsRead(Long adminId) {
        List<adminNotificationLog> logs;
        try {
            logs = jpaRepo.findByAdminIdOrderByCreatedAtDesc(adminId);
            logs.forEach(log -> log.setRead(true));
            jpaRepo.saveAll(logs); 
        } catch (Exception e) {
            logs = mongoRepo.findByAdminIdOrderByCreatedAtDesc(adminId);
            logs.forEach(log -> log.setRead(true));
        }
        try { mongoRepo.saveAll(logs); } catch (Exception e) {}
    }

    // 🔴 GATEKEEPER METHOD (Yeh toggle check karega)
    public void createNotificationWithCheck(Long adminId, String type, String title, String msg, String toggleKey) {
        Optional<adminLoginModel> adminOpt = adminService.getAdminById(adminId);
        
        if (adminOpt.isPresent()) {
            agencyNotificationSettings settings = adminOpt.get().getNotificationSettings();
            boolean isAllowed = true; 
            
            // Agar settings null hain, toh by default TRUE maan lenge
            if (settings != null) {
                // Toggles check
                switch(toggleKey) {
                    case "NEW_REGISTRATION": isAllowed = settings.getEmailNotifNewReg(); break;
                    case "BILLING_ALERT": isAllowed = settings.getEmailNotifBilling(); break;
                    case "IN_APP_NEW_MSG": isAllowed = settings.getInAppNotifNewMsg(); break;
                    case "IN_APP_CAMP_STATUS": isAllowed = settings.getInAppNotifCampStatus(); break;
                    case "IN_APP_PUSH": isAllowed = settings.getInAppNotifPush(); break;
                }
            }
            
            if (!isAllowed) {
                System.out.println("🛑 Blocked by Admin Settings: " + title);
                return; // Gatekeeper ne rok liya!
            }
            
            saveNotifToDB(adminId, type, title, msg);
        }
    }

    private void saveNotifToDB(Long adminId, String type, String title, String msg) {
        adminNotificationLog log = new adminNotificationLog();
        log.setAdminId(adminId);
        log.setType(type);
        log.setTitle(title);
        log.setMessage(msg);
        
        try {
            adminNotificationLog savedLog = jpaRepo.save(log); 
            log.setId(savedLog.getId());
            try { mongoRepo.save(log); } catch (Exception e) {}
        } catch (Exception e) {
            log.setId(System.currentTimeMillis());
            mongoRepo.save(log);
        }
    }
}