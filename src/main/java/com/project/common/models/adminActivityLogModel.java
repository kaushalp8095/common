package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Entity
@Document(collection = "admin_activity_logs_backup")
@Table(name = "adminactivitylogmodel")
public class adminActivityLogModel {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;       // Kaun admin ke under ye log hai

    private String userName;    // "Super Admin" / "Agency Mgr_01" etc.
    private String userType;    // "ADMIN" / "AGENCY" / "CLIENT"

    @Column(length = 500)
    private String action;      // "Updated System Setting" / "Failed Login Attempt"

    private String module;      // "Auth" / "Billing" / "Campaigns" / "System Setting" etc.

    private String ipAddress;   // "192.168.1.1"

    private String status;      // "SUCCESS" / "FAILED"

    private LocalDateTime logTime; // Exact timestamp

    public adminActivityLogModel() {}

    // Constructor — easy log banana ke liye
    public adminActivityLogModel(Long adminId, String userName, String userType,
                                  String action, String module, String ipAddress, String status) {
        this.adminId   = adminId;
        this.userName  = userName;
        this.userType  = userType;
        this.action    = action;
        this.module    = module;
        this.ipAddress = ipAddress;
        this.status    = status;
        this.logTime   = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLogTime() { return logTime; }
    public void setLogTime(LocalDateTime logTime) { this.logTime = logTime; }
}