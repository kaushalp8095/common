package com.project.common.repository.jpa;

import com.project.common.models.agencyLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface agencyLoginHistoryRepository extends JpaRepository<agencyLoginHistory, Long> {
    List<agencyLoginHistory> findTop5ByAgencyEmailOrderByLoginTimeDesc(String agencyEmail);
}