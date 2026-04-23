package com.project.common.repository.mongodb;

import com.project.common.models.agencyAddClientModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface agencyClientMongoRepository extends MongoRepository<agencyAddClientModel, Long> {
    
    // 1. Failover ke liye email se dhundne ki facility
    Optional<agencyAddClientModel> findByEmail(String email);
    List<agencyAddClientModel> findByAgencyId(Long agencyId);
}