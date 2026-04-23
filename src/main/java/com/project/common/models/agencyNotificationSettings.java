package com.project.common.models; 

import jakarta.persistence.Embeddable;

@Embeddable
public class agencyNotificationSettings {

    // --- Email Notifications ---
    private boolean emailNotifNewReg = true;
    private boolean emailNotifBilling = true;
    private boolean emailNotifAlerts = true;
    private boolean emailNotifSysFail = true;
    private boolean emailNotifSysReport = true;
    private boolean emailNotifNewAgency = true;

    // --- In-App Notifications ---
    private boolean inAppNotifNewMsg = true;
    private boolean inAppNotifCampStatus = true;
    private boolean inAppNotifPush = true;
    private boolean inAppNotifMention = true;
    private boolean inAppNotifMsgCom = true;
    private boolean inAppNotifDir = true;

    // ==========================================
    // GETTERS & SETTERS 
    // ==========================================
 // Email Notifications Getters/Setters
    public boolean getEmailNotifNewReg() { return emailNotifNewReg; }
    public void setEmailNotifNewReg(boolean emailNotifNewReg) { this.emailNotifNewReg = emailNotifNewReg; }

    public boolean getEmailNotifBilling() { return emailNotifBilling; }
    public void setEmailNotifBilling(boolean emailNotifBilling) { this.emailNotifBilling = emailNotifBilling; }

    public boolean getEmailNotifAlerts() { return emailNotifAlerts; }
    public void setEmailNotifAlerts(boolean emailNotifAlerts) { this.emailNotifAlerts = emailNotifAlerts; }

    public boolean getEmailNotifSysFail() { return emailNotifSysFail; }
    public void setEmailNotifSysFail(boolean emailNotifSysFail) { this.emailNotifSysFail = emailNotifSysFail; }

    public boolean getEmailNotifSysReport() { return emailNotifSysReport; }
    public void setEmailNotifSysReport(boolean emailNotifSysReport) { this.emailNotifSysReport = emailNotifSysReport; }

    public boolean getEmailNotifNewAgency() { return emailNotifNewAgency; }
    public void setEmailNotifNewAgency(boolean emailNotifNewAgency) { this.emailNotifNewAgency = emailNotifNewAgency; }

    // In-App Notifications Getters/Setters
    public boolean getInAppNotifNewMsg() { return inAppNotifNewMsg; }
    public void setInAppNotifNewMsg(boolean inAppNotifNewMsg) { this.inAppNotifNewMsg = inAppNotifNewMsg; }

    public boolean getInAppNotifCampStatus() { return inAppNotifCampStatus; }
    public void setInAppNotifCampStatus(boolean inAppNotifCampStatus) { this.inAppNotifCampStatus = inAppNotifCampStatus; }

    public boolean getInAppNotifPush() { return inAppNotifPush; }
    public void setInAppNotifPush(boolean inAppNotifPush) { this.inAppNotifPush = inAppNotifPush; }

    public boolean getInAppNotifMention() { return inAppNotifMention; }
    public void setInAppNotifMention(boolean inAppNotifMention) { this.inAppNotifMention = inAppNotifMention; }

    public boolean getInAppNotifMsgCom() { return inAppNotifMsgCom; }
    public void setInAppNotifMsgCom(boolean inAppNotifMsgCom) { this.inAppNotifMsgCom = inAppNotifMsgCom; }

    public boolean getInAppNotifDir() { return inAppNotifDir; }
    public void setInAppNotifDir(boolean inAppNotifDir) { this.inAppNotifDir = inAppNotifDir; 
   }
  }