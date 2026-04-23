package com.project.common.repository.mongodb;

import com.project.common.models.adminAddAgenciesModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface adminAgenciesMongoRepository extends MongoRepository<adminAddAgenciesModel, Long> {
    // Yahan MongoDB specific queries likh sakte hain
	Optional<adminAddAgenciesModel> findByEmail(String email);
	boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    
    List<adminAddAgenciesModel> findByAdminId(Long adminId);
}