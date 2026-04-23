package com.project.common.repository.jpa;

import com.project.common.models.adminLoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface adminLoginRepository extends JpaRepository<adminLoginModel, Long> {
    Optional<adminLoginModel> findByEmail(String email);
}