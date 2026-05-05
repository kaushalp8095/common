package com.project.common.repository.mongodb;

import com.project.common.models.adminScheduledReportModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface adminScheduledReportMongoRepository extends MongoRepository<adminScheduledReportModel, Long> {
    List<adminScheduledReportModel> findByAdminIdOrderByNextRunDateAsc(Long adminId);
}