package com.project.common.models;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;

@Entity
@Document(collection = "agency_clients_backup")
@Table(name = "agencyaddclientmodel")
public class agencyAddClientModel {

	@jakarta.persistence.Id  // SQL Primary Key
    @org.springframework.data.annotation.Id // MongoDB Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long agencyId;
    private String agencyName;
    private String clientName;
    private String email;
    private String contactNumber;

    

    // Default Constructor
    public agencyAddClientModel() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAgencyId() { return agencyId; }
    public void setAgencyId(Long agencyId) { this.agencyId = agencyId; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

}