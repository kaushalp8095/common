package com.project.common.repository.mongodb;

import com.project.common.models.adminLoginModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface adminLoginMongoRepository extends MongoRepository<adminLoginModel, Long> {
    Optional<adminLoginModel> findByEmail(String email);
}