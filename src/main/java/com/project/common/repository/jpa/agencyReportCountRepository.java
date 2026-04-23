package com.project.common.repository.jpa;

import com.project.common.models.agencyReportCountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface agencyReportCountRepository extends JpaRepository<agencyReportCountModel, Long> {
    
    // Agency ID ke base par count find karne ke liye
    agencyReportCountModel findByAgencyId(Long agencyId);
}