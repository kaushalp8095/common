package com.project.common.repository.jpa;

import com.project.common.models.adminReportModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public interface adminReportJpaRepository extends JpaRepository<adminReportModel, Long> {

    List<adminReportModel> findByAdminIdOrderByGeneratedOnDesc(Long adminId);

    // Filter by type
    List<adminReportModel> findByAdminIdAndReportTypeOrderByGeneratedOnDesc(Long adminId, String type);

    // Count by type — donut chart ke liye
    @Query("SELECT r.reportType, COUNT(r) FROM adminReportModel r WHERE r.adminId = :adminId GROUP BY r.reportType")
    List<Object[]> countByType(@Param("adminId") Long adminId);

    // Monthly count — bar chart ke liye (last 6 months)
    @Query(value = "SELECT TO_CHAR(generated_on, 'Mon') as month, COUNT(*) as count " +
                   "FROM adminreportmodel WHERE admin_id = :adminId " +
                   "AND generated_on >= CURRENT_DATE - INTERVAL '6 months' " +
                   "GROUP BY TO_CHAR(generated_on, 'Mon'), DATE_TRUNC('month', generated_on) " +
                   "ORDER BY DATE_TRUNC('month', generated_on)", nativeQuery = true)
    List<Object[]> getMonthlyReportCount(@Param("adminId") Long adminId);
}