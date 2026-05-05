package com.project.common.repository.mongodb;

import com.project.common.models.adminReportModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface adminReportMongoRepository extends MongoRepository<adminReportModel, Long> {
    List<adminReportModel> findByAdminIdOrderByGeneratedOnDesc(Long adminId);
    List<adminReportModel> findByAdminIdAndReportTypeOrderByGeneratedOnDesc(Long adminId, String type);
}