package com.project.common.repository.jpa;

import com.project.common.models.adminActivityLogModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Primary
public interface adminActivityLogJpaRepository extends JpaRepository<adminActivityLogModel, Long> {

    // ==========================================
    // 1. Sabhi logs — latest pehle
    // ==========================================
    List<adminActivityLogModel> findByAdminIdOrderByLogTimeDesc(Long adminId);

    // ==========================================
    // 2. Search + Date Filter (JPQL query)
    // search: userName, action, module, ipAddress me dhundo
    // startDate / endDate: date range filter
    // ==========================================
    @Query("SELECT l FROM adminActivityLogModel l WHERE l.adminId = :adminId " +
           "AND (:search IS NULL OR :search = '' OR " +
           "     LOWER(l.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(l.action)   LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(l.module)   LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(l.ipAddress) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:startDate IS NULL OR l.logTime >= :startDate) " +
           "AND (:endDate IS NULL OR l.logTime <= :endDate) " +
           "ORDER BY l.logTime DESC")
    List<adminActivityLogModel> searchLogs(
            @Param("adminId")   Long adminId,
            @Param("search")    String search,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate")   LocalDateTime endDate
    );
}