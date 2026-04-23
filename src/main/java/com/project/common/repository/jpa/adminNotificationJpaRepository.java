package com.project.common.repository.jpa;

import com.project.common.models.adminNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface adminNotificationJpaRepository extends JpaRepository<adminNotificationLog, Long> {
    List<adminNotificationLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}