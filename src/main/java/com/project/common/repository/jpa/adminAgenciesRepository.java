package com.project.common.repository.jpa;

import com.project.common.models.adminAddAgenciesModel;
import com.project.common.dto.adminAgenciesStatsdto; // DTO Import kiya

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface adminAgenciesRepository extends JpaRepository<adminAddAgenciesModel, Long> {

    Optional<adminAddAgenciesModel> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

 // 🔴 Naya Method: Admin ID ke basis par agencies fetch karne ke liye (Dropdown ke liye)
    List<adminAddAgenciesModel> findByAdminId(Long adminId);

    // 🔴 Stats query mein bhi AdminId ka filter add karein taaki Admin ko sirf apni agencies dikhein
    @Query("SELECT a.id AS id, a.agencyName AS agencyName, a.ownerName AS ownerName, " +
           "a.email AS email, a.phoneNumber AS phoneNumber, a.agencyLogo AS agencyLogo, a.status AS status, " +
           "(SELECT COUNT(c.id) FROM agencyAddClientModel c WHERE c.agencyId = a.id) AS totalClients, " +
           "(SELECT COUNT(camp.id) FROM agencyAddCampaignModel camp WHERE camp.agencyId = a.id) AS totalCampaigns, " +
           "(SELECT COALESCE(SUM(camp.budget), 0) FROM agencyAddCampaignModel camp WHERE camp.agencyId = a.id) AS totalRevenue " +
           "FROM adminAddAgenciesModel a WHERE a.adminId = :adminId") // 👈 Filter added
    List<adminAgenciesStatsdto> getAgenciesByAdminIdWithStats(@Param("adminId") Long adminId);
    
    
}