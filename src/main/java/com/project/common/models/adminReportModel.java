package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Entity
@Document(collection = "admin_reports_backup")
@Table(name = "adminreportmodel")
public class adminReportModel {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    private String reportName;      // "Campaign Summary Q2"
    private String reportType;      // "Performance Report" / "Financial Summary" / "Client Activity" / "System Logs"
    private String generatedBy;     // "Super Admin"
    private String status;          // "Generated" / "Pending" / "Failed"
    private String format;          // "PDF" / "Excel" / "CSV"

    private LocalDate startDate;    // Report ka date range
    private LocalDate endDate;

    // Aggregated data JSON me store — frontend charts ke liye
    @Column(columnDefinition = "TEXT")
    private String reportData;      // JSON: {totalSpend, conversions, avgCost, chartData:[...]}

    private LocalDate generatedOn;

    public adminReportModel() {}

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReportData() { return reportData; }
    public void setReportData(String reportData) { this.reportData = reportData; }

    public LocalDate getGeneratedOn() { return generatedOn; }
    public void setGeneratedOn(LocalDate generatedOn) { this.generatedOn = generatedOn; }
}