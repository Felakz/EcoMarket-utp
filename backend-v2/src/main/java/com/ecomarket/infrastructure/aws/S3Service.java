package com.ecomarket.infrastructure.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Servicio para manejo de archivos en AWS S3
 */
@Service
@Slf4j
public class S3Service {
    
    @Value("${aws.s3.bucket-name:ecomarket-products}")
    private String bucketName;
    
    @Value("${aws.s3.region:us-east-1}")
    private String region;
    
    @Value("${aws.s3.access-key:}")
    private String accessKey;
    
    @Value("${aws.s3.secret-key:}")
    private String secretKey;
    
    @Value("${aws.s3.enabled:false}")
    private boolean s3Enabled;
    
    private S3Client s3Client;
    
    /**
     * Inicializa el cliente S3
     */
    private S3Client getS3Client() {
        if (s3Client == null && s3Enabled) {
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
        return s3Client;
    }
    
    /**
     * Sube un archivo a S3
     */
    public String uploadFile(String key, InputStream inputStream, String contentType, long contentLength) {
        if (!s3Enabled) {
            log.warn("S3 is disabled. File not uploaded: {}", key);
            return "local://" + key;
        }
        
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();
            
            getS3Client().putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength));
            
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("File uploaded to S3: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    /**
     * Descarga un archivo de S3
     */
    public InputStream downloadFile(String key) {
        if (!s3Enabled) {
            throw new RuntimeException("S3 is disabled");
        }
        
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            return getS3Client().getObject(getRequest);
        } catch (Exception e) {
            log.error("Error downloading file from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
    
    /**
     * Elimina un archivo de S3
     */
    public void deleteFile(String key) {
        if (!s3Enabled) {
            log.warn("S3 is disabled. File not deleted: {}", key);
            return;
        }
        
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            getS3Client().deleteObject(deleteRequest);
            log.info("File deleted from S3: {}", key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
    
    /**
     * Genera una URL pública para un archivo
     */
    public String getFileUrl(String key) {
        if (!s3Enabled) {
            return "local://" + key;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }
}
