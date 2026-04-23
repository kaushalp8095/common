package com.project.common.models;

import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Id; // Dono ke liye common use ho jayega
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Document(collection = "agency_integration_backup")
@Table(name = "agency_integration_model")
public class agencyIntegrationModel {
	
	@jakarta.persistence.Id  // SQL Primary Key
    @org.springframework.data.annotation.Id // MongoDB Primary Key
    private String agencyEmail; // Email ko hi Primary Key bana dete hain (SQL aur Mongo dono ke liye)

    // Google Ads Tokens
    @Column(length = 500)
    private String googleRefreshToken;
    private String googleCustomerId; 
    private boolean isGoogleConnected = false;

    // Facebook Meta Tokens
    @Column(length = 500)
    private String fbAccessToken;
    private String fbPageId;
    private boolean isFbConnected = false;

    // Default Constructor
    public agencyIntegrationModel() {}

    // Getters and Setters
    public String getAgencyEmail() { return agencyEmail; }
    public void setAgencyEmail(String agencyEmail) { this.agencyEmail = agencyEmail; }

    public String getGoogleRefreshToken() { return googleRefreshToken; }
    public void setGoogleRefreshToken(String googleRefreshToken) { this.googleRefreshToken = googleRefreshToken; }

    public String getGoogleCustomerId() { return googleCustomerId; }
    public void setGoogleCustomerId(String googleCustomerId) { this.googleCustomerId = googleCustomerId; }

    public boolean isGoogleConnected() { return isGoogleConnected; }
    public void setGoogleConnected(boolean isGoogleConnected) { this.isGoogleConnected = isGoogleConnected; }

    public String getFbAccessToken() { return fbAccessToken; }
    public void setFbAccessToken(String fbAccessToken) { this.fbAccessToken = fbAccessToken; }

    public String getFbPageId() { return fbPageId; }
    public void setFbPageId(String fbPageId) { this.fbPageId = fbPageId; }

    public boolean isFbConnected() { return isFbConnected; }
    public void setFbConnected(boolean isFbConnected) { this.isFbConnected = isFbConnected; }
}