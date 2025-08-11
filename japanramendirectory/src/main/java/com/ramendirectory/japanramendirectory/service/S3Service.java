package com.ramendirectory.japanramendirectory.service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ramendirectory.japanramendirectory.model.Comment;
import com.ramendirectory.japanramendirectory.repository.CommentRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    
    @Autowired
    private S3Client s3Client;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${app.comment.allowed-content-types}")
    private String allowedContentTypes;
    
    /**
     * Upload a file to S3 and return the public URL.
     * 
     * @param file The file to upload
     * @param userId The ID of the user uploading the file
     * @param commentId The ID of the comment the photo is associated with
     * @return The public URL of the uploaded file
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If the file is invalid or has an invalid content type
     */
    public String uploadFile(MultipartFile file, Long userId, Long commentId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Invalid content type. Allowed types: " + allowedContentTypes);
        }
        
        // Generate unique file name to avoid collisions
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Create a unique key with timestamp and UUID to avoid collisions
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueKey = "comments/" + userId + "/" + commentId + "/" + timestamp + "_" + UUID.randomUUID() + extension;
        
        logger.info("Uploading file to S3: {}", uniqueKey);
        
        // Upload file to S3
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .contentType(contentType)
                .build();
        
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        
        // Generate the public URL for the file
        URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .build());
        
        String photoUrl = url.toString();
        logger.info("File uploaded successfully. URL: {}", photoUrl);
        
        // Add the photo URL to the comment
        addPhotoToComment(commentId, photoUrl);
        
        return photoUrl;
    }
    
    /**
     * Add a photo URL to a comment
     * 
     * @param commentId The ID of the comment to add the photo to
     * @param photoUrl The URL of the photo to add
     */
    public void addPhotoToComment(Long commentId, String photoUrl) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        comment.addPhoto(photoUrl);
        commentRepository.save(comment);
        logger.info("Added photo URL to comment {}: {}", commentId, photoUrl);
    }
    
    /**
     * Check if the content type is allowed.
     * 
     * @param contentType The content type to check
     * @return True if the content type is allowed, false otherwise
     */
    private boolean isAllowedContentType(String contentType) {
        if (allowedContentTypes == null || allowedContentTypes.isEmpty()) {
            return false;
        }
        
        String[] allowedTypes = allowedContentTypes.split(",");
        for (String allowedType : allowedTypes) {
            if (allowedType.trim().equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        
        return false;
    }
} 