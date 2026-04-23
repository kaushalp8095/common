package com.project.common.service;

import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.common.models.adminInvoiceModel;
import com.project.common.repository.jpa.adminInvoiceRepository;
import com.project.common.repository.mongodb.adminInvoiceMongoRepository;

@Service
public class InvoiceService {

    @Autowired
    private adminInvoiceRepository sqlRepo;

    @Autowired
    private adminInvoiceMongoRepository mongoRepo;

    // ==========================================
    // 1. SAVE INVOICE (Dual DB Sync)
    // ==========================================
    public synchronized adminInvoiceModel saveInvoice(adminInvoiceModel invoice) {
        try {
            // 1. 🔴 AUTO-CALCULATION (Backend Safety)
            // Agar frontend se calculations galat aayin toh backend usey sahi kar dega
            double rate = (invoice.getRate() != null) ? invoice.getRate() : 0.0;
            double taxPercent = (invoice.getTaxPercent() != null) ? invoice.getTaxPercent() : 0.0;
            
            double taxAmount = (rate * taxPercent) / 100;
            double finalTotal = rate + taxAmount;
            
            invoice.setTotalAmount(finalTotal); // Final sacchai database mein save hogi

         // ==========================================
         // 2. 🔴 AUTO-INVOICE NUMBER (Sequential Logic)
         // ==========================================
            
         if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().isEmpty()) {
             
             // 1. Prefix banao (e.g., #INV-2026-APR-)
             String year = String.valueOf(LocalDate.now().getYear());
             String month = LocalDate.now().getMonth().name().substring(0, 3).toUpperCase();
             String prefix = "#INV-" + year + "-" + month + "-";

             // 2. SQL se aakhri invoice dhundo (Source of Truth)
             adminInvoiceModel lastInvoice = sqlRepo.findTopByInvoiceNoStartingWithOrderByInvoiceNoDesc(prefix);
             
             // 3. Agar SQL mein na mile toh Mongo se dhundo
             if (lastInvoice == null) {
                 lastInvoice = mongoRepo.findFirstByInvoiceNoStartingWithOrderByInvoiceNoDesc(prefix);
             }
             
             // 4. Sequence logic
             int nextNumber = 1;
             if (lastInvoice != null) {
                 String lastNo = lastInvoice.getInvoiceNo();
                 // Prefix ke baad ka hissa nikaalo (0001)
                 String lastSeq = lastNo.substring(prefix.length()); 
                 nextNumber = Integer.parseInt(lastSeq) + 1;
             }
             
             // 5. Final Format: #INV-2026-APR-0001
             String newInvoiceNo = prefix + String.format("%04d", nextNumber);
             invoice.setInvoiceNo(newInvoiceNo);
         }

            // 3. 🔴 STATUS DEFAULT
            if (invoice.getStatus() == null) {
                invoice.setStatus("Pending");
            }

            // --- DUAL DB SAVE LOGIC ---
            adminInvoiceModel savedInSql = sqlRepo.save(invoice);
            invoice.setId(savedInSql.getId()); // Sync ID for Mongo
            
            try {
                mongoRepo.save(invoice);
            } catch (Exception e) {
                System.err.println("Mongo Sync Failed: " + e.getMessage());
            }

            return savedInSql;
        } catch (Exception e) {
            throw new RuntimeException("Save failed: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. GET DASHBOARD DATA (Stats + Table)
    // ==========================================
    public Map<String, Object> getBillingDashboardData(Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Stats from SQL
            Double totalBilled = sqlRepo.getTotalBilledByAdmin(adminId);
            Double paidAmount = sqlRepo.getTotalPaidByAdmin(adminId);
            // 🔴 Naya Field: Current Month Revenue
            Double monthlyRev = sqlRepo.getCurrentMonthRevenue(adminId);
            
            double total = (totalBilled != null) ? totalBilled : 0.0;
            double paid = (paidAmount != null) ? paidAmount : 0.0;
            double monthly = (monthlyRev != null) ? monthlyRev : 0.0;
            
            response.put("totalBilled", total);
            response.put("paidAmount", paid);
            response.put("outstanding", total - paid);
            response.put("currentMonthRevenue", monthly); // 👈 Ye frontend ke 4th card ke liye hai
            
            // Table data using DTO
            response.put("invoices", sqlRepo.findInvoicesForTableByAdmin(adminId));
         // Chart Data from Supabase
            response.put("chartData", sqlRepo.getMonthlyRevenueStats(adminId));
            
        } catch (Exception e) {
            System.err.println("⚠️ SQL Down! Fetching basic data from Mongo...");
            List<adminInvoiceModel> mongoInvoices = mongoRepo.findByAdminId(adminId);
            response.put("invoices", mongoInvoices);
            response.put("totalBilled", 0.0);
            response.put("paidAmount", 0.0);
            response.put("outstanding", 0.0);
            response.put("currentMonthRevenue", 0.0);
            System.err.println("SQL Error, fetching Chart from Mongo...");
            // Chart Data from Mongo (Failover)
            response.put("chartData", mongoRepo.getMonthlyRevenueStatsMongo(adminId));
        }
        return response;
    }

    // ==========================================
    // 3. GET SINGLE INVOICE DETAILS
    // ==========================================
    public Optional<adminInvoiceModel> getInvoiceById(Long id) {
        try {
            Optional<adminInvoiceModel> sqlInvoice = sqlRepo.findById(id);
            if(sqlInvoice.isPresent()) return sqlInvoice;
            return mongoRepo.findById(id);
        } catch (Exception e) {
            return mongoRepo.findById(id);
        }
    }

    // ==========================================
    // 4. DELETE INVOICE
    // ==========================================
    public void deleteInvoice(Long id) {
        try { sqlRepo.deleteById(id); } catch (Exception e) { System.err.println("SQL Delete Error"); }
        try { mongoRepo.deleteById(id); } catch (Exception e) { System.err.println("Mongo Delete Error"); }
    }
}