package com.project.common.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.s3.endpoint}")
    private String s3Endpoint;

    @Value("${supabase.s3.accessKey}")
    private String accessKey;

    @Value("${supabase.s3.secretKey}")
    private String secretKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    @Value("${supabase.url}")
    private String publicBaseUrl;

    private S3Client s3Client;

    // 🔴 IMPROVEMENT: S3Client ko ek hi baar initialize karein (App start hote waqt)
    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(s3Endpoint))
                .region(Region.of("ap-south-1")) 
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();
    }

    /**
     * Files upload karne ke liye (Optimized Version)
     */
    public String uploadFile(MultipartFile file, String folderName) throws Exception {
        if (file.isEmpty()) throw new Exception("File is empty");

        try {
            // 1. Unique File Name
            String originalFileName = file.getOriginalFilename();
            String extension = (originalFileName != null && originalFileName.contains(".")) 
                               ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                               : "";
            
            // Spaces aur special chars remove karne ke liye unique ID use kar rahe hain
            String fileName = folderName + "/" + UUID.randomUUID().toString() + extension;

            // 2. Upload Request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // 3. Uploading bytes
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 4. Final Public URL
            // Format: [SupabaseURL]/storage/v1/object/public/[bucket]/[folder]/[file]
            String finalUrl = String.format("%s/storage/v1/object/public/%s/%s", 
                                            publicBaseUrl, bucketName, fileName);
            
            System.out.println("🚀 Uploaded: " + finalUrl);
            return finalUrl;

        } catch (Exception e) {
            System.err.println("❌ Storage Error: " + e.getMessage());
            throw new Exception("Supabase S3 Error: " + e.getMessage());
        }
    }
    
    /**
     * Admin Profile Logo Update Karne ke bad Supabase se delete karne ke liye
     */
    public void deleteFileFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        try {
            
            String bucketPath = "/public/" + bucketName + "/";
            int index = fileUrl.indexOf(bucketPath);
            
            if (index != -1) {
                // Key nikal li (e.g., "admin-logos/1234-uuid.jpg")
                String objectKey = fileUrl.substring(index + bucketPath.length());
                
                // Supabase S3 delete request
                software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteRequest = 
                    software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();
                        
                s3Client.deleteObject(deleteRequest);
                System.out.println("🗑️ Deleted Old Logo from Supabase: " + objectKey);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to delete old logo: " + e.getMessage());
        }
    }
}