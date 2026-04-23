package com.project.common.dto;

public interface adminAgenciesStatsdto {
    // Agency ki basic fields
    Long getId();
    String getAgencyName();
    String getOwnerName();
    String getEmail();
    String getPhoneNumber();
    String getAgencyLogo();
    String getStatus();
    
    // Aggregated (Calculated) fields
    Long getTotalClients();
    Long getTotalCampaigns();
    Double getTotalRevenue();
}