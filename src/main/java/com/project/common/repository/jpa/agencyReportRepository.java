package com.project.common.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.common.models.agencyAddCampaignModel;

@Repository
public interface agencyReportRepository extends JpaRepository<agencyAddCampaignModel, Long> {
    
    // Agency ID filter add kiya gaya hai
    @Query(value = "SELECT * FROM agencyaddcampaignmodel WHERE agency_id = :agencyId", nativeQuery = true)
    List<agencyAddCampaignModel> findAllCampaignsForReports(@Param("agencyId") Long agencyId);
}