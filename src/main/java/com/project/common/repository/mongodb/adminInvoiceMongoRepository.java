package com.project.common.repository.mongodb;

import com.project.common.models.adminInvoiceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Aggregation;
import java.util.List;


@Repository
public interface adminInvoiceMongoRepository extends MongoRepository<adminInvoiceModel, Long> {

    // 1. Admin ID ke basis par saare invoices fetch karne ke liye (Backup flow)
    List<adminInvoiceModel> findByAdminId(Long adminId);

    // 2. Specific Agency ke invoices filter karne ke liye
    List<adminInvoiceModel> findByAgencyId(Long agencyId);

    // 3. Status wise filter (Optional, dashboard backup ke liye)
    List<adminInvoiceModel> findByAdminIdAndStatus(Long adminId, String status);
    
 // 4. Invoice number se search karne ke liye
    adminInvoiceModel findByInvoiceNo(String invoiceNo);
    
 // 5. MongoDB aggregation pichle 6 mahine ke liye
    @Aggregation(pipeline = {
    	    "{ $match: { adminId: ?0, status: 'Paid' } }",
    	    "{ $group: { _id: { $dateToString: { format: '%b', date: '$issueDate' } }, total: { $sum: '$totalAmount' }, monthDate: { $first: '$issueDate' } } }",
    	    "{ $sort: { monthDate: 1 } }", // Date ke basis par sahi sort hoga
    	    "{ $project: { month: '$_id', total: 1, _id: 0 } }"
    	})
    	List<java.util.Map<String, Object>> getMonthlyRevenueStatsMongo(Long adminId);
    
 // 6. Ye method sabse latest invoice dhundhega jo aapke prefix (e.g., #INV-2026-APR-) se start hota hai
      adminInvoiceModel findFirstByInvoiceNoStartingWithOrderByInvoiceNoDesc(String prefix);
 
}