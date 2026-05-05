package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Entity
@Document(collection = "admin_scheduled_reports_backup")
@Table(name = "adminscheduledreportmodel")
public class adminScheduledReportModel {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;

    private String scheduleName;    // "Weekly Campaign Report"
    private String reportType;      // "Performance Report" / "Financial Summary" etc.
    private String frequency;       // "Daily" / "Weekly" / "Monthly"
    private LocalDate nextRunDate;
    private String recipients;      // Comma-separated emails
    private String status;          // "Active" / "Paused"

    public adminScheduledReportModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDate getNextRunDate() { return nextRunDate; }
    public void setNextRunDate(LocalDate nextRunDate) { this.nextRunDate = nextRunDate; }

    public String getRecipients() { return recipients; }
    public void setRecipients(String recipients) { this.recipients = recipients; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}