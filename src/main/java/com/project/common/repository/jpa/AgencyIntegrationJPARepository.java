package com.project.common.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.common.models.agencyIntegrationModel;

public interface AgencyIntegrationJPARepository extends JpaRepository<agencyIntegrationModel, String> {
    // String yahan agencyEmail (Primary Key) hai
}