package com.project.common.repository.mongodb;

import com.project.common.models.agencyAddCampaignModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface agencyReportMongoRepository extends MongoRepository<agencyAddCampaignModel, Long> {
    
    // MongoDB filter by Agency ID
    List<agencyAddCampaignModel> findByAgencyId(Long agencyId);
}