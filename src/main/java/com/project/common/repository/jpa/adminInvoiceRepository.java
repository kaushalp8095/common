package com.project.common.repository.jpa;

import com.project.common.models.adminInvoiceModel;
import com.project.common.dto.adminInvoiceTableDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface adminInvoiceRepository extends JpaRepository<adminInvoiceModel, Long> {

    // 1. Recent Invoices Table (Saare statuses ke liye)
    @Query("SELECT new com.project.common.dto.adminInvoiceTableDTO(i.id, i.invoiceNo, i.agencyName, i.issueDate, i.totalAmount, i.status) " +
           "FROM adminInvoiceModel i WHERE i.adminId = :adminId ORDER BY i.issueDate DESC")
    List<adminInvoiceTableDTO> findInvoicesForTableByAdmin(@Param("adminId") Long adminId);

    // 2. Total Billed (KPI Card 1): Saare Invoices (Pending + Paid)
    // Isme filter nahi hai, isliye ye pura business dikhayega
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM adminInvoiceModel i WHERE i.adminId = :adminId")
    Double getTotalBilledByAdmin(@Param("adminId") Long adminId);

    // 3. Paid Amount (KPI Card 2): Sirf 'Paid' status wale
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM adminInvoiceModel i WHERE i.adminId = :adminId AND i.status = 'Paid'")
    Double getTotalPaidByAdmin(@Param("adminId") Long adminId);
    
    // 4. This Month Revenue (KPI Card 4): Is mahine ka sirf 'Paid' revenue
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM adminInvoiceModel i " +
           "WHERE i.adminId = :adminId " +
           "AND i.status = 'Paid' " +
           "AND i.issueDate >= CAST(date_trunc('month', CURRENT_DATE) AS date)") 
    Double getCurrentMonthRevenue(@Param("adminId") Long adminId);
    
 // Query 5: Native Query mein table ka naam sahi karein
    @Query(value = "SELECT to_char(issue_date, 'Mon') as month, " +
           "COALESCE(SUM(total_amount), 0) as total " +
           "FROM admin_invoices " + // 🔴 'admin_invoice_model' ko badal kar 'admin_invoices' karein
           "WHERE admin_id = :adminId AND status = 'Paid' " +
           "AND issue_date > CURRENT_DATE - INTERVAL '6 months' " +
           "GROUP BY to_char(issue_date, 'Mon'), date_trunc('month', issue_date) " +
           "ORDER BY date_trunc('month', issue_date)", nativeQuery = true)
    List<java.util.Map<String, Object>> getMonthlyRevenueStats(@Param("adminId") Long adminId);
    
 // Query 6: Ye method sabse latest invoice dhundhega jo aapke prefix (e.g., #INV-2026-APR-) se start hota hai
       adminInvoiceModel findTopByInvoiceNoStartingWithOrderByInvoiceNoDesc(String prefix);
 
}