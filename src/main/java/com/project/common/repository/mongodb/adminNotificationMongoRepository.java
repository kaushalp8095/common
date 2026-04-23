package com.project.common.repository.mongodb;

import com.project.common.models.adminNotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface adminNotificationMongoRepository extends MongoRepository<adminNotificationLog, Long> {
    List<adminNotificationLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}