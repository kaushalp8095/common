package com.project.common.service;

import com.project.common.models.adminRoleModel;
import com.project.common.repository.jpa.adminRoleJpaRepository;
import com.project.common.repository.mongodb.adminRoleMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminRoleService {

    @Autowired
    private adminRoleJpaRepository sqlRepo;

    @Autowired
    private adminRoleMongoRepository mongoRepo;

    // ==========================================
    // 1. SAVE (Create + Update dono)
    // ==========================================
    public adminRoleModel saveRole(adminRoleModel role) {

        // Role name trim
        if (role.getRoleName() != null) {
            role.setRoleName(role.getRoleName().trim());
        }

        boolean isNew = (role.getId() == null || role.getId() == 0);

        // Duplicate check sirf naye role ke liye
        if (isNew) {
            if (sqlRepo.existsByAdminIdAndRoleNameAndRoleType(
                    role.getAdminId(), role.getRoleName(), role.getRoleType())) {
                throw new RuntimeException("Error: Is naam ka role pehle se exist karta hai!");
            }
        } else {
            if (sqlRepo.existsByAdminIdAndRoleNameAndRoleTypeAndIdNot(
                    role.getAdminId(), role.getRoleName(), role.getRoleType(), role.getId())) {
                throw new RuntimeException("Error: Is naam ka role pehle se exist karta hai!");
            }
        }

        // 1. SQL (Supabase/PostgreSQL) me save karo
        adminRoleModel savedRole = null;
        try {
            savedRole = sqlRepo.save(role);
            role.setId(savedRole.getId()); // MongoDB ke liye same ID set karo
            System.out.println("✅ Role SQL Save Success. ID: " + savedRole.getId());
        } catch (Exception e) {
            System.err.println("❌ Role SQL Save Error: " + e.getMessage());
            throw new RuntimeException("Database error: " + e.getMessage());
        }

        // 2. MongoDB me bhi save karo (same ID)
        try {
            mongoRepo.save(role);
            System.out.println("✅ Role MongoDB Save Success.");
        } catch (Exception e) {
            System.err.println("❌ Role MongoDB Save Error: " + e.getMessage());
        }

        return savedRole;
    }

    // ==========================================
    // 2. LIST (AdminId + RoleType ke basis par)
    // ==========================================
    public List<adminRoleModel> getRolesByAdminAndType(Long adminId, String roleType) {
        try {
            List<adminRoleModel> list = sqlRepo.findByAdminIdAndRoleType(adminId, roleType);
            if (list != null && !list.isEmpty()) return list;
            // Failover to MongoDB
            return mongoRepo.findByAdminIdAndRoleType(adminId, roleType);
        } catch (Exception e) {
            System.err.println("❌ SQL failed, trying MongoDB: " + e.getMessage());
            return mongoRepo.findByAdminIdAndRoleType(adminId, roleType);
        }
    }

    // ==========================================
    // 3. GET BY ID
    // ==========================================
    public Optional<adminRoleModel> getRoleById(Long id) {
        try {
            Optional<adminRoleModel> sqlData = sqlRepo.findById(id);
            if (sqlData.isPresent()) return sqlData;
            return mongoRepo.findById(id);
        } catch (Exception e) {
            return mongoRepo.findById(id);
        }
    }

    // ==========================================
    // 4. DELETE
    // ==========================================
    public void deleteRole(Long id) {
        try {
            sqlRepo.deleteById(id);
            System.out.println("✅ Role SQL Delete Success. ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ Role SQL Delete Error: " + e.getMessage());
        }

        try {
            mongoRepo.deleteById(id);
            System.out.println("✅ Role MongoDB Delete Success. ID: " + id);
        } catch (Exception e) {
            System.err.println("❌ Role MongoDB Delete Error: " + e.getMessage());
        }
    }
}