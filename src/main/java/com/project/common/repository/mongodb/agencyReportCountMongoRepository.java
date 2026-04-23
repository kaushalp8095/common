package com.project.common.repository.mongodb;

import com.project.common.models.agencyReportCountModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface agencyReportCountMongoRepository extends MongoRepository<agencyReportCountModel, Long> {
    
    // MongoDB fallback ke liye same method
    agencyReportCountModel findByAgencyId(Long agencyId);
}