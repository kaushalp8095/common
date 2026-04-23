package com.project.common.dto;

import java.time.LocalDate;

public class adminInvoiceTableDTO {
    private Long id;
    private String invoiceNo;
    private String agencyName; 
    private LocalDate issueDate;
    private Double totalAmount;
    private String status;

    // Constructor, Getters and Setters
    public adminInvoiceTableDTO(Long id, String invoiceNo, String agencyName, LocalDate issueDate, Double totalAmount, String status) {
        this.id = id;
        this.invoiceNo = invoiceNo;
        this.agencyName = agencyName;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    // Getters...
    
}