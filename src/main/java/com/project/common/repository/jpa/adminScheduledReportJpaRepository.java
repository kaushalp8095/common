// ===== adminScheduledReportJpaRepository.java =====
package com.project.common.repository.jpa;

import com.project.common.models.adminScheduledReportModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Primary
public interface adminScheduledReportJpaRepository extends JpaRepository<adminScheduledReportModel, Long> {
    List<adminScheduledReportModel> findByAdminIdOrderByNextRunDateAsc(Long adminId);
}


// ===== adminScheduledReportMongoRepository.java =====
// (Alag file me daalna — yahan sirf reference ke liye)
// package com.project.common.repository.mongodb;
// import com.project.common.models.adminScheduledReportModel;
// import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.stereotype.Repository;
// import java.util.List;
// @Repository
// public interface adminScheduledReportMongoRepository extends MongoRepository<adminScheduledReportModel, Long> {
//     List<adminScheduledReportModel> findByAdminIdOrderByNextRunDateAsc(Long adminId);
// }