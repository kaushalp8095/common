package com.project.common.service;

import com.project.common.models.agencyAddCampaignModel;
import com.project.common.repository.jpa.agencyLocationRepository;
import com.project.common.repository.mongodb.agencyLocationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LocationService {

    @Autowired
    private agencyLocationRepository sqlRepo;

    @Autowired
    private agencyLocationMongoRepository mongoRepo;

    public List<Map<String, Object>> getAllLocationAnalytics(Long agencyId) {
        try {
            // Primary: Fetch from Supabase
            return sqlRepo.getLocationAnalytics(agencyId); 
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Location Analytics from Mongo...");
            
            // 🔴 DOUBLE SAFETY NET
            try {
                // Fallback: Fetch from MongoDB
                return mongoRepo.getLocationAnalytics(agencyId);
            } catch (Exception ex) {
                System.err.println("❌ Critical Error: Both Databases Failed for Location Analytics.");
                return new ArrayList<>(); // Empty list bhej do taaki Frontend crash na ho
            }
        }
    }

    public List<agencyAddCampaignModel> findCampaignsByLocation(String name, Long agencyId) {
        try {
            // Primary: Fetch from Supabase
            return sqlRepo.findCampaignsByLocationName(name, agencyId);
        } catch (Exception e) {
            System.err.println("⚠️ Supabase Down! Fetching Campaigns by Location from Mongo...");
            
            // 🔴 DOUBLE SAFETY NET
            try {
                // Fallback: Fetch from MongoDB
                return mongoRepo.findByTargetLocationAndAgencyId(name, agencyId);
            } catch (Exception ex) {
                System.err.println("❌ Critical Error: Both Databases Failed for Campaigns by Location.");
                return new ArrayList<>(); // Empty list bhej do taaki Frontend crash na ho
            }
        }
    }
}