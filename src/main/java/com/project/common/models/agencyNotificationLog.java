package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Entity
@Document(collection = "agency_notification_logs_backup") // MongoDB Collection
@Table(name = "agency_notification_logs") // Supabase Table
public class agencyNotificationLog {

	@jakarta.persistence.Id  // SQL Primary Key
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Supabase ke liye Long ID best hai

    private String agencyEmail;
    
    private String type; 
    private String title;
    private String message;
    
    private boolean isRead = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();

    // ==========================
    // GETTERS & SETTERS
    // ==========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAgencyEmail() { return agencyEmail; }
    public void setAgencyEmail(String agencyEmail) { this.agencyEmail = agencyEmail; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}