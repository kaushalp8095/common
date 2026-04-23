package com.project.common.service;

import com.project.common.models.agencyAddClientModel;
import com.project.common.models.adminAddAgenciesModel;
import com.project.common.repository.jpa.agencyClientRepository;
import com.project.common.repository.mongodb.agencyClientMongoRepository;
import com.project.common.repository.jpa.adminAgenciesRepository;
import com.project.common.repository.mongodb.adminAgenciesMongoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseSyncService {

    // Client Repositories
    @Autowired
    private agencyClientRepository clientSqlRepo;

    @Autowired
    private agencyClientMongoRepository clientMongoRepo;

    // Agency Repositories
    @Autowired
    private adminAgenciesRepository agencySqlRepo;

    @Autowired
    private adminAgenciesMongoRepository agencyMongoRepo;
    /**
     * SYNC CLIENTS: MongoDB -> Supabase (SQL)
     * Jab Supabase down ho aur Mongo mein entry giri ho, 
     * toh online aane ke baad ye SQL mein sync karega.
     */
    @Async
    @Scheduled(fixedDelay = 600000) // Har 10 minute mein chalega
    public void autoSyncClientsToSql() {
        try {
            // Check if SQL is up
            clientSqlRepo.count();

            List<agencyAddClientModel> mongoData = clientMongoRepo.findAll();
            for (agencyAddClientModel mongoClient : mongoData) {
                // Check if already in SQL using email
                boolean existsInSql = clientSqlRepo.findByEmail(mongoClient.getEmail()).isPresent();

                if (!existsInSql) {
                    try {
                        agencyAddClientModel syncClient = new agencyAddClientModel();
                        syncClient.setAgencyId(mongoClient.getAgencyId());
                        syncClient.setClientName(mongoClient.getClientName());
                        syncClient.setAgencyName(mongoClient.getAgencyName());
                        syncClient.setEmail(mongoClient.getEmail());
                        syncClient.setContactNumber(mongoClient.getContactNumber());

                        // 1. Save to SQL
                        agencyAddClientModel savedInSql = clientSqlRepo.save(syncClient);

                        // 2. Sync IDs (Mongo ID ko SQL ID se replace karein)
                        clientMongoRepo.deleteById(mongoClient.getId());
                        clientMongoRepo.save(savedInSql);

                        System.out.println("✅ Sync: Client [" + savedInSql.getEmail() + "] moved to SQL.");
                    } catch (Exception e) {
                        System.err.println("❌ Sync Error for Client: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // Supabase is still down, ignore and try in next cycle
        }
    }

    /**
     * SYNC AGENCIES: Supabase (SQL) -> MongoDB
     * Ye un entries ke liye hai jo SQL mein toh hain par Mongo mein nahi 
     * (Jaise aapki ID 27 wali purani entries).
     */
    @Async
    @Scheduled(fixedDelay = 600000) // Har 10 minute mein chalega
    public void autoSyncAgenciesToMongo() {
        try {
            List<adminAddAgenciesModel> sqlAgencies = agencySqlRepo.findAll();
            for (adminAddAgenciesModel sqlAgency : sqlAgencies) {
                // Check if ID exists in MongoDB
                boolean existsInMongo = agencyMongoRepo.existsById(sqlAgency.getId());

                if (!existsInMongo) {
                    try {
                        // SQL data ko Mongo mein save karein
                        agencyMongoRepo.save(sqlAgency);
                        System.out.println("✅ Sync: Old Agency ID [" + sqlAgency.getId() + "] moved to MongoDB.");
                    } catch (Exception e) {
                        System.err.println("❌ Mongo Sync Error for Agency: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // Database errors handle
        }
    }
}