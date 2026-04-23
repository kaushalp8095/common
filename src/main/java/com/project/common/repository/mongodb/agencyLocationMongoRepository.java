package com.project.common.repository.mongodb;

import com.project.common.models.agencyAddCampaignModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface agencyLocationMongoRepository extends MongoRepository<agencyAddCampaignModel, Long> {

    // $match add kiya taaki sirf us agency ka data process ho
    @Aggregation(pipeline = {
        "{ '$match': { 'agencyId': ?0 } }", // <--- YAHAN FILTER ADD KIYA
        "{ '$group': { '_id': { 'targetLocation': '$targetLocation', 'clientName': '$clientName' }, " +
        "'leads': { '$sum': '$leads' }, " +
        "'totalConversions': { '$sum': '$totalConversions' }, " +
        "'conversionRate': { '$avg': '$conversionRate' }, " +
        "'activeCampaigns': { '$sum': { '$cond': [ { '$eq': ['$status', 'Active'] }, 1, 0 ] } } } }",
        "{ '$project': { 'targetLocation': '$_id.targetLocation', 'clientName': '$_id.clientName', 'leads': 1, 'totalConversions': 1, 'conversionRate': 1, 'activeCampaigns': 1, '_id': 0 } }"
    })
    List<Map<String, Object>> getLocationAnalytics(Long agencyId);

    // Filter location and agency
    List<agencyAddCampaignModel> findByTargetLocationAndAgencyId(String targetLocation, Long agencyId);
}