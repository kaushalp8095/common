package com.project.common.service;

import com.project.common.models.adminReportModel;
import com.project.common.models.adminScheduledReportModel;
import com.project.common.repository.jpa.adminReportJpaRepository;
import com.project.common.repository.jpa.adminScheduledReportJpaRepository;
import com.project.common.repository.mongodb.adminReportMongoRepository;
import com.project.common.repository.mongodb.adminScheduledReportMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AdminReportService {

    @Autowired private adminReportJpaRepository sqlRepo;
    @Autowired private adminReportMongoRepository mongoRepo;
    @Autowired private adminScheduledReportJpaRepository schedSqlRepo;
    @Autowired private adminScheduledReportMongoRepository schedMongoRepo;

    // ==========================================
    // 1. SAVE REPORT (dual DB)
    // ==========================================
    public adminReportModel saveReport(adminReportModel report) {
        if (report.getGeneratedOn() == null) report.setGeneratedOn(LocalDate.now());
        if (report.getStatus()      == null) report.setStatus("Generated");
        if (report.getGeneratedBy() == null) report.setGeneratedBy("Super Admin");

        adminReportModel saved = null;
        try {
            saved = sqlRepo.save(report);
            report.setId(saved.getId());
        } catch (Exception e) { System.err.println("❌ Report SQL Save: " + e.getMessage()); }
        try { mongoRepo.save(report); } catch (Exception e) { System.err.println("❌ Report Mongo: " + e.getMessage()); }
        return saved != null ? saved : report;
    }

    // ==========================================
    // 2. GET ALL REPORTS
    // ==========================================
    public List<adminReportModel> getAllReports(Long adminId) {
        try {
            List<adminReportModel> list = sqlRepo.findByAdminIdOrderByGeneratedOnDesc(adminId);
            if (list != null && !list.isEmpty()) return list;
            return mongoRepo.findByAdminIdOrderByGeneratedOnDesc(adminId);
        } catch (Exception e) {
            return mongoRepo.findByAdminIdOrderByGeneratedOnDesc(adminId);
        }
    }

    // ==========================================
    // 3. GET REPORT BY ID
    // ==========================================
    public Optional<adminReportModel> getReportById(Long id) {
        try {
            Optional<adminReportModel> r = sqlRepo.findById(id);
            if (r.isPresent()) return r;
            return mongoRepo.findById(id);
        } catch (Exception e) { return mongoRepo.findById(id); }
    }

    // ==========================================
    // 4. DELETE REPORT
    // ==========================================
    public void deleteReport(Long id) {
        try { sqlRepo.deleteById(id); } catch (Exception e) { System.err.println("❌ Report SQL Delete: " + e.getMessage()); }
        try { mongoRepo.deleteById(id); } catch (Exception e) { System.err.println("❌ Report Mongo Delete: " + e.getMessage()); }
    }

    // ==========================================
    // 5. CHART DATA — donut (type-wise) + bar (monthly)
    // ==========================================
    public Map<String, Object> getChartData(Long adminId) {
        Map<String, Object> charts = new LinkedHashMap<>();

        // Donut chart — report type wise count
        try {
            List<Object[]> typeData = sqlRepo.countByType(adminId);
            List<String> labels = new ArrayList<>();
            List<Long>   counts = new ArrayList<>();
            for (Object[] row : typeData) {
                labels.add(row[0].toString());
                counts.add(((Number) row[1]).longValue());
            }
            charts.put("typeLabels", labels);
            charts.put("typeCounts", counts);
        } catch (Exception e) {
            // Fallback from all reports
            List<adminReportModel> all = mongoRepo.findByAdminIdOrderByGeneratedOnDesc(adminId);
            Map<String, Long> typeCounts = new LinkedHashMap<>();
            for (adminReportModel r : all) {
                String type = r.getReportType() != null ? r.getReportType() : "Other";
                typeCounts.merge(type, 1L, Long::sum);
            }
            charts.put("typeLabels", new ArrayList<>(typeCounts.keySet()));
            charts.put("typeCounts", new ArrayList<>(typeCounts.values()));
        }

        // Bar chart — last 6 months monthly count
        try {
            List<Object[]> monthData = sqlRepo.getMonthlyReportCount(adminId);
            List<String> months = new ArrayList<>();
            List<Long>   mCounts = new ArrayList<>();
            for (Object[] row : monthData) {
                months.add(row[0].toString());
                mCounts.add(((Number) row[1]).longValue());
            }
            charts.put("monthLabels", months);
            charts.put("monthCounts", mCounts);
        } catch (Exception e) {
            // Fallback: generate last 6 months with 0
            List<String> months  = new ArrayList<>();
            List<Long>   mCounts = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                months.add(LocalDate.now().minusMonths(i)
                        .getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                mCounts.add(0L);
            }
            charts.put("monthLabels", months);
            charts.put("monthCounts", mCounts);
        }

        return charts;
    }

    // ==========================================
    // 6. STATS SUMMARY
    // ==========================================
    public Map<String, Object> getStats(Long adminId) {
        List<adminReportModel> all = getAllReports(adminId);
        long total     = all.size();
        long generated = all.stream().filter(r -> "Generated".equalsIgnoreCase(r.getStatus())).count();
        long pending   = all.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalReports",     total);
        stats.put("generatedReports", generated);
        stats.put("pendingReports",   pending);
        return stats;
    }

    // ==========================================
    // SCHEDULED REPORTS
    // ==========================================
    public adminScheduledReportModel saveSchedule(adminScheduledReportModel schedule) {
        if (schedule.getStatus() == null) schedule.setStatus("Active");
        adminScheduledReportModel saved = null;
        try { saved = schedSqlRepo.save(schedule); schedule.setId(saved.getId()); } catch (Exception e) {}
        try { schedMongoRepo.save(schedule); } catch (Exception e) {}
        return saved != null ? saved : schedule;
    }

    public List<adminScheduledReportModel> getSchedules(Long adminId) {
        try {
            List<adminScheduledReportModel> list = schedSqlRepo.findByAdminIdOrderByNextRunDateAsc(adminId);
            if (list != null && !list.isEmpty()) return list;
            return schedMongoRepo.findByAdminIdOrderByNextRunDateAsc(adminId);
        } catch (Exception e) {
            return schedMongoRepo.findByAdminIdOrderByNextRunDateAsc(adminId);
        }
    }

    public void deleteSchedule(Long id) {
        try { schedSqlRepo.deleteById(id); } catch (Exception e) {}
        try { schedMongoRepo.deleteById(id); } catch (Exception e) {}
    }
}