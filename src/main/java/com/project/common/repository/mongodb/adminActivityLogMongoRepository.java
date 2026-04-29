package com.project.common.repository.mongodb;

import com.project.common.models.adminActivityLogModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface adminActivityLogMongoRepository extends MongoRepository<adminActivityLogModel, Long> {

    List<adminActivityLogModel> findByAdminIdOrderByLogTimeDesc(Long adminId);
}