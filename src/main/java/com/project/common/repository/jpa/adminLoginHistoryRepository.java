package com.project.common.repository.jpa;

import com.project.common.models.adminLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface adminLoginHistoryRepository extends JpaRepository<adminLoginHistory, Long> {
    
    // Admin ID ke basis par latest 10 login records nikalne ke liye
    List<adminLoginHistory> findTop10ByAdminIdOrderByLoginTimeDesc(Long adminId);
}

