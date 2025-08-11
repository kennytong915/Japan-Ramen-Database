package com.ramendirectory.japanramendirectory.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ramendirectory.japanramendirectory.model.Comment;
import com.ramendirectory.japanramendirectory.repository.CommentRepository;
import com.ramendirectory.japanramendirectory.service.S3Service;
import com.ramendirectory.japanramendirectory.util.JwtUtil;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    
    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);
    
    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${app.comment.max-photos}")
    private int maxPhotosPerComment;
    
    /**
     * Upload photos for a specific comment.
     * 
     * @param commentId The ID of the comment to add photos to
     * @param files The files to upload (up to maxPhotosPerComment)
     * @param jwt The authenticated user's JWT
     * @return A response with URLs of the uploaded photos
     */
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> uploadPhotosForComment(
            @PathVariable Long commentId,
            @RequestParam("files") MultipartFile[] files,
            @AuthenticationPrincipal Jwt jwt) {
        
        Long userId = jwtUtil.getUserIdFromJwt(jwt);
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User authentication failed");
        }
        
        // Find the comment
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comment not found");
        }
        
        // Check if the comment belongs to the user
        if (!comment.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only upload photos to your own comments");
        }
        
        // Check if the number of photos doesn't exceed the limit
        List<String> existingPhotos = comment.getPhotos();
        if (existingPhotos.size() + files.length > maxPhotosPerComment) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Maximum " + maxPhotosPerComment + " photos allowed per comment");
        }
        
        List<String> uploadedPhotoUrls = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();
        
        // Upload each photo
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            
            try {
                String photoUrl = s3Service.uploadFile(file, userId, commentId);
                uploadedPhotoUrls.add(photoUrl);
                
                // Add the photo URL to the comment
                comment.addPhoto(photoUrl);
                
            } catch (IOException e) {
                logger.error("Error uploading file: {}", e.getMessage());
                errors.put("file_" + i, "Error uploading: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid file: {}", e.getMessage());
                errors.put("file_" + i, e.getMessage());
            }
        }
        
        // Save the comment with the new photos
        commentRepository.save(comment);
        
        // Return the response with URLs and any errors
        Map<String, Object> response = new HashMap<>();
        response.put("uploadedPhotos", uploadedPhotoUrls);
        
        if (!errors.isEmpty()) {
            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
} 