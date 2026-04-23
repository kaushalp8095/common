package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Table(name = "agencyreportcountmodel")
@Document(collection = "agencyreportcountmodel_backup") // Mongo ke liye
public class agencyReportCountModel {

	@jakarta.persistence.Id  // SQL Primary Key
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agency_id", unique = true, nullable = false)
    private Long agencyId;

    @Column(name = "report_download_count")
    private Long reportDownloadCount = 0L;

    // Default Constructor
    public agencyReportCountModel() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getReportDownloadCount() {
        return reportDownloadCount;
    }

    public void setReportDownloadCount(Long reportDownloadCount) {
        this.reportDownloadCount = reportDownloadCount;
    }
}