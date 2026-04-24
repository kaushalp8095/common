package com.project.common.repository.jpa;

import com.project.common.models.adminRoleModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public interface adminRoleJpaRepository extends JpaRepository<adminRoleModel, Long> {

    // AdminId + RoleType ke basis par list (ADMIN ya AGENCY)
    List<adminRoleModel> findByAdminIdAndRoleType(Long adminId, String roleType);

    // Duplicate role name check
    boolean existsByAdminIdAndRoleNameAndRoleType(Long adminId, String roleName, String roleType);

    // Edit ke time duplicate check (apna ID chhod ke)
    boolean existsByAdminIdAndRoleNameAndRoleTypeAndIdNot(Long adminId, String roleName, String roleType, Long id);
}