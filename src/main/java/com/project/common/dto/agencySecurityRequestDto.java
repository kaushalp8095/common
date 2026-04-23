package com.project.common.dto;

public class agencySecurityRequestDto {
	
    private String email;
    private String currentPassword;
    private String newPassword;
    
    private Boolean is2faEnabled;
    private String tfaMethod;
    private Boolean loginAlertsEnabled;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public Boolean getIs2faEnabled() { return is2faEnabled; }
    public void setIs2faEnabled(Boolean is2faEnabled) { this.is2faEnabled = is2faEnabled; }

    public String getTfaMethod() { return tfaMethod; }
    public void setTfaMethod(String tfaMethod) { this.tfaMethod = tfaMethod; }

    public Boolean getLoginAlertsEnabled() { return loginAlertsEnabled; }
    public void setLoginAlertsEnabled(Boolean loginAlertsEnabled) { this.loginAlertsEnabled = loginAlertsEnabled; }
}