package com.project.common.repository.mongodb;

import com.project.common.models.adminLoginHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface adminLoginHistoryMongoRepository extends MongoRepository<adminLoginHistory, Long> {
    // Mongo mein sirf save ke liye use hoga, fetch hum SQL se hi karenge
}