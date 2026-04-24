package com.project.common.models;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "admin_roles_backup")
@Table(name = "adminrolemodel")
public class adminRoleModel {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId; // Kis Admin ne ye Role banaya

    // "ADMIN" ya "AGENCY" — dono alag tables me dikhenge
    private String roleType;

    private String roleName;

    @Column(length = 1000)
    private String description;

    // Permissions JSON string ke roop me store hongi
    // Example: {"dashboard":{"read":true,"write":false,"delete":false},"agency":{"read":true,...}}
    @Column(columnDefinition = "TEXT")
    private String permissions;

    // Kitne users is role pe hain (count for display)
    private Integer userCount = 0;

    public adminRoleModel() {}

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
}