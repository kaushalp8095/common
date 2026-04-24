package com.project.common.repository.mongodb;

import com.project.common.models.adminRoleModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface adminRoleMongoRepository extends MongoRepository<adminRoleModel, Long> {

    List<adminRoleModel> findByAdminIdAndRoleType(Long adminId, String roleType);

    boolean existsByAdminIdAndRoleNameAndRoleType(Long adminId, String roleName, String roleType);
}