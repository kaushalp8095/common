package com.project.common.repository.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.common.models.agencyAddCampaignModel;

@Repository
public interface agencyAddCampaignRepository extends JpaRepository<agencyAddCampaignModel, Long> {
    
    List<agencyAddCampaignModel> findByClientIdAndAgencyId(Long clientId, Long agencyId);

    List<agencyAddCampaignModel> findByAgencyId(Long agencyId);

    // 🔴 UPDATED METHOD WITH ADMIN ID FILTER
    @Query(value = "SELECT camp.id, " +
            "camp.campaign_name AS \"campaignName\", " +
            "ag.agency_name AS \"agencyName\", " +
            "cl.client_name AS \"clientName\", " +
            "camp.start_date AS \"startDate\", " +
            "camp.end_date AS \"endDate\", " +
            "camp.budget AS \"budget\", " +
            "camp.status AS \"status\" " + 
            "FROM agencyaddcampaignmodel camp " +
            "LEFT JOIN adminaddagenciesmodel ag ON camp.agency_id = ag.id " +
            "LEFT JOIN agencyaddclientmodel cl ON camp.client_id = cl.id " +
            "WHERE ag.admin_id = :adminId " + // 👈 Filter by Admin ID
            "ORDER BY camp.id DESC", 
            nativeQuery = true)
    List<Map<String, Object>> findAllCampaignsForAdmin(@Param("adminId") Long adminId);
}