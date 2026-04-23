package com.project.common.repository.jpa;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.common.models.agencyAddCampaignModel;

public interface agencyLocationRepository extends JpaRepository<agencyAddCampaignModel, Long> {
    
    @Query(value = "SELECT target_location AS \"targetLocation\", " +
            "client_name AS \"clientName\", " + 
            "COUNT(CASE WHEN status = 'Active' THEN 1 END) AS \"activeCampaigns\", " +
            "SUM(COALESCE(leads, 0)) AS \"leads\", " +
            "SUM(COALESCE(total_conversions, 0)) AS \"totalConversions\", " +
            "AVG(COALESCE(conversion_rate, 0)) AS \"conversionRate\" " +
            "FROM agencyaddcampaignmodel " +
            "WHERE agency_id = :agencyId " + // <--- YAHAN FILTER ADD KIYA
            "GROUP BY target_location, client_name", 
            nativeQuery = true)
    List<Map<String, Object>> getLocationAnalytics(@Param("agencyId") Long agencyId);
    
    // Details page ke liye bhi future-proof kar lein
    @Query(value = "SELECT * FROM agencyaddcampaignmodel WHERE target_location = :locationName AND agency_id = :agencyId", nativeQuery = true)
    List<agencyAddCampaignModel> findCampaignsByLocationName(@Param("locationName") String locationName, @Param("agencyId") Long agencyId);
}