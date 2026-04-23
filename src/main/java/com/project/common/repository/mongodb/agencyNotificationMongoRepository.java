package com.project.common.repository.mongodb;

import com.project.common.models.agencyNotificationLog;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface agencyNotificationMongoRepository extends MongoRepository<agencyNotificationLog, Long> {
	List<agencyNotificationLog> findByAgencyEmailOrderByCreatedAtDesc(String email);
}