package com.project.common.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.common.models.agencyAddClientModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface agencyClientRepository extends JpaRepository<agencyAddClientModel, Long> {

	Optional<agencyAddClientModel> findByEmail(String email);
	
	@Query(value = "SELECT c.id, " +
	        "c.client_name AS \"clientName\", " +
	        "c.agency_name AS \"agencyName\", " +
	        "c.email, " +
	        "c.contact_number AS \"contactNumber\", " +
	        "COUNT(camp.id) AS \"totalCampaigns\", " +
	        "COUNT(CASE WHEN camp.status = 'Active' THEN 1 END) AS \"activeCampaignsCount\", " +
	        "SUM(COALESCE(camp.leads, 0)) AS \"leads\", " +
	        "AVG(COALESCE(camp.conversion_rate, 0)) AS \"conversionRate\", " +
	        "MAX(camp.target_location) AS \"location\" " + 
	        "FROM agencyaddclientmodel c " +
	        "LEFT JOIN agencyaddcampaignmodel camp ON c.id = camp.client_id " +
	        "WHERE c.agency_id = :agencyId " + 
	        "GROUP BY c.id, c.client_name, c.agency_name, c.email, c.contact_number", 
	    nativeQuery = true)
	// Yahan bracket me Long agencyId add kiya hai
	List<Map<String, Object>> getClientsWithPerformanceMetricsByAgency(@Param("agencyId") Long agencyId);

 }