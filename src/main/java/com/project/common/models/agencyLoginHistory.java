package com.project.common.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Table(name = "agency_login_history")
@Document(collection = "backup_agency_login_history")
public class agencyLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id
    private Long id;

    private String agencyEmail;
    private String deviceInfo;
    private String location;
    private String ipAddress;
    private LocalDateTime loginTime;

    // Getters aur Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAgencyEmail() { return agencyEmail; }
    public void setAgencyEmail(String agencyEmail) { this.agencyEmail = agencyEmail; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}