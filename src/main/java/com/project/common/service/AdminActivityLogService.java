package com.project.common.service;

import com.project.common.models.adminActivityLogModel;
import com.project.common.repository.jpa.adminActivityLogJpaRepository;
import com.project.common.repository.mongodb.adminActivityLogMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminActivityLogService {

    @Autowired
    private adminActivityLogJpaRepository sqlRepo;

    @Autowired
    private adminActivityLogMongoRepository mongoRepo;

    // ==========================================
    // 1. LOG SAVE (dono DB me) — dusre controllers call karenge
    // ==========================================
    public void saveLog(adminActivityLogModel log) {
        if (log.getLogTime() == null) {
            log.setLogTime(LocalDateTime.now());
        }
        try {
            adminActivityLogModel saved = sqlRepo.save(log);
            log.setId(saved.getId());
            System.out.println("✅ Activity Log SQL Save. ID: " + saved.getId());
        } catch (Exception e) {
            System.err.println("❌ Activity Log SQL Error: " + e.getMessage());
        }
        try {
            mongoRepo.save(log);
            System.out.println("✅ Activity Log MongoDB Save.");
        } catch (Exception e) {
            System.err.println("❌ Activity Log MongoDB Error: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. QUICK LOG — shortcut method (dusre controllers ke liye)
    // ==========================================
    public void log(Long adminId, String userName, String userType,
                    String action, String module, String ipAddress, String status) {
        adminActivityLogModel log = new adminActivityLogModel(
                adminId, userName, userType, action, module, ipAddress, status);
        saveLog(log);
    }

    // ==========================================
    // 3. GET ALL LOGS (no filter)
    // ==========================================
    public List<adminActivityLogModel> getAllLogs(Long adminId) {
        try {
            List<adminActivityLogModel> list = sqlRepo.findByAdminIdOrderByLogTimeDesc(adminId);
            if (list != null && !list.isEmpty()) return list;
            return mongoRepo.findByAdminIdOrderByLogTimeDesc(adminId);
        } catch (Exception e) {
            System.err.println("❌ SQL failed, MongoDB fallback: " + e.getMessage());
            return mongoRepo.findByAdminIdOrderByLogTimeDesc(adminId);
        }
    }

    // ==========================================
    // 4. SEARCH + DATE FILTER
    // dateRange: "TODAY" / "YESTERDAY" / "LAST_7" / "LAST_30" / "THIS_MONTH" / "CUSTOM"
    // ==========================================
    public List<adminActivityLogModel> searchLogs(Long adminId, String search,
                                                   String dateRange,
                                                   String customStart, String customEnd) {
        LocalDateTime startDate = null;
        LocalDateTime endDate   = null;
        LocalDate today         = LocalDate.now();

        switch (dateRange == null ? "" : dateRange.toUpperCase()) {
            case "TODAY":
                startDate = today.atStartOfDay();
                endDate   = today.atTime(LocalTime.MAX);
                break;
            case "YESTERDAY":
                startDate = today.minusDays(1).atStartOfDay();
                endDate   = today.minusDays(1).atTime(LocalTime.MAX);
                break;
            case "LAST_7":
                startDate = today.minusDays(6).atStartOfDay();
                endDate   = today.atTime(LocalTime.MAX);
                break;
            case "LAST_30":
                startDate = today.minusDays(29).atStartOfDay();
                endDate   = today.atTime(LocalTime.MAX);
                break;
            case "THIS_MONTH":
                startDate = today.withDayOfMonth(1).atStartOfDay();
                endDate   = today.atTime(LocalTime.MAX);
                break;
            case "CUSTOM":
                // Frontend se "2025-07-01" format me aayega
                if (customStart != null && !customStart.isBlank())
                    startDate = LocalDate.parse(customStart).atStartOfDay();
                if (customEnd != null && !customEnd.isBlank())
                    endDate = LocalDate.parse(customEnd).atTime(LocalTime.MAX);
                break;
            default:
                // No date filter
                break;
        }

        String searchTerm = (search == null || search.isBlank()) ? null : search.trim();

        try {
            return sqlRepo.searchLogs(adminId, searchTerm, startDate, endDate);
        } catch (Exception e) {
            System.err.println("❌ Search SQL Error, MongoDB fallback: " + e.getMessage());
            // MongoDB me simple fallback — sab le lo aur Java me filter karo
            List<adminActivityLogModel> all = mongoRepo.findByAdminIdOrderByLogTimeDesc(adminId);
            if (searchTerm == null) return all;
            final String term = searchTerm.toLowerCase();
            all.removeIf(l ->
                !safeContains(l.getUserName(), term) &&
                !safeContains(l.getAction(),   term) &&
                !safeContains(l.getModule(),   term) &&
                !safeContains(l.getIpAddress(),term)
            );
            return all;
        }
    }

    private boolean safeContains(String field, String term) {
        return field != null && field.toLowerCase().contains(term);
    }
}