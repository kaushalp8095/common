package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Table(name = "admin_login_master")
@Document(collection = "admin_login_backup")
public class adminLoginModel {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.annotation.Id 
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Column(length = 500)
    private String profileLogo;
    
    @Column(name = "login_alerts_enabled")
    private boolean loginAlertsEnabled = true;

    // 🔴 NAYA ADD KIYA GAYA CODE YAHAN HAI
    @Embedded
    private agencyNotificationSettings notificationSettings = new agencyNotificationSettings();

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getProfileLogo() { return profileLogo; }
    public void setProfileLogo(String profileLogo) { this.profileLogo = profileLogo; }
    
    public boolean isLoginAlertsEnabled() { return loginAlertsEnabled; }
    public void setLoginAlertsEnabled(boolean loginAlertsEnabled) { this.loginAlertsEnabled = loginAlertsEnabled; }

    // 🔴 Getters & Setters for Notification Settings
    public agencyNotificationSettings getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(agencyNotificationSettings notificationSettings) { 
        this.notificationSettings = notificationSettings; 
    }
}