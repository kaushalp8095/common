package com.project.common.repository.mongodb;

import com.project.common.models.agencyLoginHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface agencyLoginHistoryMongoRepository extends MongoRepository<agencyLoginHistory, Long> {
    List<agencyLoginHistory> findTop5ByAgencyEmailOrderByLoginTimeDesc(String agencyEmail);
}