package com.project.common.models;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;

@Entity
@Document(collection = "agency_campaigns_backup")
@Table(name = "agencyaddcampaignmodel")
public class agencyAddCampaignModel {

	@jakarta.persistence.Id  // SQL Primary Key
    @org.springframework.data.annotation.Id // MongoDB Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private Long adminId;
    private Long agencyId; 
    private Long clientId;

    private String campaignName;
    private String clientName;
    
    @Column(length = 1000)
    private String targetLocation;
    
    private String startDate;
    private String endDate;
    private Double budget;
    
    private Integer leads;
    private Integer totalConversions; 
    private Double conversionRate;   

    private String gender;
    private String ageRange;

    private String status = "Active";

    @Column(length = 3000)
    private String geoData;
    
    private String adPlatform;

    private String creativePath;

    public agencyAddCampaignModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getAgencyId() { return agencyId; }
    public void setAgencyId(Long agencyId) { this.agencyId = agencyId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTargetLocation() { return targetLocation; }
    public void setTargetLocation(String targetLocation) { this.targetLocation = targetLocation; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Integer getLeads() { return leads; }
    public void setLeads(Integer leads) { this.leads = leads; }

    public Integer getTotalConversions() { return totalConversions; }
    public void setTotalConversions(Integer totalConversions) { this.totalConversions = totalConversions; }

    public Double getConversionRate() { return conversionRate; }
    public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGeoData() { return geoData; }
    public void setGeoData(String geoData) { this.geoData = geoData; }
    
    public String getAdPlatform() {
		return adPlatform;
	}

	public void setAdPlatform(String adPlatform) {
		this.adPlatform = adPlatform;
	}

	public String getCreativePath() { return creativePath; }
    public void setCreativePath(String creativePath) { this.creativePath = creativePath; }
}