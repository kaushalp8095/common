package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "admin_agencies_backup")
@Table(name = "adminaddagenciesmodel")
public class adminAddAgenciesModel {
	@jakarta.persistence.Id //  SQL 
    @org.springframework.data.annotation.Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private Long adminId; // Kis Admin ne ye Agency banayi hai
    private String agencyName;
    private String agencyLogo;
    private String ownerName;
    private String email;
    private String password;
    private String phoneNumber;
    private String plan;
    private String status;
    
    // Dono address lines ko yahan merge karke store karenge
    @Column(length = 500)
    private String address; 
    private String country;
    private String state;
    private String city;
    private String pincode;
    
    // --- SECURITY PREFERENCES ---
    @Column(name = "is_2fa_enabled")
    private boolean is2faEnabled = false;

    @Column(name = "tfa_method")
    private String tfaMethod = "email"; // "email" ya "sms"

    @Column(name = "login_alerts_enabled")
    private boolean loginAlertsEnabled = true;

    //  NOTIFICATION SETTINGS FIELD
    @Embedded
    private agencyNotificationSettings notificationSettings = new agencyNotificationSettings();

    public adminAddAgenciesModel() {
        super();
    }

    public adminAddAgenciesModel(Long id, Long adminId, String agencyName, String agencyLogo, String ownerName, String email,
            String password, String phoneNumber, String plan, String status, String address, String country,
            String state, String city, String pincode, boolean is2faEnabled, String tfaMethod,
            boolean loginAlertsEnabled, agencyNotificationSettings notificationSettings) {
        super();
        this.id = id;
        this.adminId = adminId;
        this.agencyName = agencyName;
        this.agencyLogo = agencyLogo;
        this.ownerName = ownerName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.plan = plan;
        this.status = status;
        this.address = address;
        this.country = country;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
        this.is2faEnabled = is2faEnabled;
        this.tfaMethod = tfaMethod;
        this.loginAlertsEnabled = loginAlertsEnabled;
        this.notificationSettings = (notificationSettings != null) ? notificationSettings : new agencyNotificationSettings();
    }

    // ==========================================
    // EXISTING GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyLogo() {
        return agencyLogo;
    }

    public void setAgencyLogo(String agencyLogo) {
        this.agencyLogo = agencyLogo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public boolean isIs2faEnabled() {
        return is2faEnabled;
    }

    public void setIs2faEnabled(boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    public String getTfaMethod() {
        return tfaMethod;
    }

    public void setTfaMethod(String tfaMethod) {
        this.tfaMethod = tfaMethod;
    }

    public boolean isLoginAlertsEnabled() {
        return loginAlertsEnabled;
    }

    public void setLoginAlertsEnabled(boolean loginAlertsEnabled) {
        this.loginAlertsEnabled = loginAlertsEnabled;
    }

    //  NOTIFICATION SETTINGS GETTER/SETTER
    public agencyNotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(agencyNotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
    
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

}