package com.project.common.repository.jpa;

import com.project.common.models.agencyNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface agencyNotificationJpaRepository extends JpaRepository<agencyNotificationLog, Long> {
    List<agencyNotificationLog> findByAgencyEmailOrderByCreatedAtDesc(String agencyEmail);
}