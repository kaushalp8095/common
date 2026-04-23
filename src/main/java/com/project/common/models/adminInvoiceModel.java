package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Entity // PostgreSQL (SQL) ke liye
@Document(collection = "admin_invoices_backup") // MongoDB ke liye
@Table(name = "admin_invoices") // SQL table ka naam
public class adminInvoiceModel {

    @jakarta.persistence.Id // SQL Primary Key
    @org.springframework.data.annotation.Id // MongoDB ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;       // Kis Admin ne banaya
    private Long agencyId;      // Kis Agency ke liye bana
    private String agencyName;

    private String invoiceNo;   // Auto-generated jise humne JS mein banaya tha
    
    @Column(length = 500)
    private String billToAddress;
    private String contactPerson;
    
    private String plan;        // Basic, Pro, Premium
    private Double rate;        // Base amount
    private Double taxPercent;  // Default 18%
    private Double totalAmount; // Rate + Tax calculation result

    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;      // Paid, Pending, Draft
    
    @Column(columnDefinition = "TEXT")
    private String notes;

    // --- CONSTRUCTORS ---
    public adminInvoiceModel() {
        super();
    }

    public adminInvoiceModel(Long id, Long adminId, Long agencyId, String agencyName, String invoiceNo,
                             String billToAddress, String contactPerson, String plan, Double rate, 
                             Double taxPercent, Double totalAmount, LocalDate issueDate, 
                             LocalDate dueDate, String status, String notes) {
        super();
        this.id = id;
        this.adminId = adminId;
        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.invoiceNo = invoiceNo;
        this.billToAddress = billToAddress;
        this.contactPerson = contactPerson;
        this.plan = plan;
        this.rate = rate;
        this.taxPercent = taxPercent;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getAgencyId() { return agencyId; }
    public void setAgencyId(Long agencyId) { this.agencyId = agencyId; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public String getBillToAddress() { return billToAddress; }
    public void setBillToAddress(String billToAddress) { this.billToAddress = billToAddress; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public Double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(Double taxPercent) { this.taxPercent = taxPercent; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}