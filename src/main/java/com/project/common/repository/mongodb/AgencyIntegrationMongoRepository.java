package com.project.common.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.common.models.agencyIntegrationModel;

public interface AgencyIntegrationMongoRepository extends MongoRepository<agencyIntegrationModel, String> {
    // MongoDB backup ke liye
}