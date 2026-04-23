package com.project.common.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.project.common.models.agencyAddCampaignModel;
import java.util.List;

@Repository
public interface agencyAddCampaignMongoRepository extends MongoRepository<agencyAddCampaignModel, Long> {
    
    List<agencyAddCampaignModel> findByClientName(String clientName);
    
    List<agencyAddCampaignModel> findByClientId(Long clientId);
    
    List<agencyAddCampaignModel> findByAgencyId(Long agencyId);

    // 🔴 Ye add karein taaki Service Layer mein error na aaye backup ke waqt
    List<agencyAddCampaignModel> findByClientIdAndAgencyId(Long clientId, Long agencyId);
    
    
 // 🔴 UPDATED METHOD FOR ADMIN BACKUP
    // Filter by adminId instead of fetching all
    List<agencyAddCampaignModel> findByAdminId(Long adminId);
    
}