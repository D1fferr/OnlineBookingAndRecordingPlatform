package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.config.ExternalConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ExternalConfig config;
    private final S3Client s3Client;


    @PostConstruct
    public void initBucket() {
        String bucketName = config.getMinio().getBucketName();
        try {
            log.info("Initialising minio bucket '{}'", bucketName);
            s3Client.headBucket(builder -> builder.bucket(bucketName));
        } catch (Exception e) {
            s3Client.createBucket(builder -> builder.bucket(bucketName));
            log.info("Minio bucket created '{}'", bucketName);

            setBucketPublicPolicy(bucketName);
        }
    }

    private void setBucketPublicPolicy(String bucketName) {
        String policy = "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": \"*\",\n" +
                "      \"Action\": [\"s3:GetObject\"],\n" +
                "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            s3Client.putBucketPolicy(builder -> builder
                    .bucket(bucketName)
                    .policy(policy));
            log.info("Public read policy set for bucket '{}'", bucketName);
        } catch (Exception e) {
            log.error("Failed to set public policy for bucket '{}': {}", bucketName, e.getMessage());
        }
    }
    public String storeImage(MultipartFile imageFile, UUID id) throws Exception{
        String bucketName = config.getMinio().getBucketName();
        String fileName = generateFileName(imageFile.getOriginalFilename(), id);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(imageFile.getContentType())
                .build();
        log.info("Trying to save image: '{}'", fileName);
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageFile.getBytes()));
        log.info("Image saved: '{}'", fileName);
        return getImageUrl(fileName);
    }

    private String getImageUrl(String fileName){
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        return "/api/images/" + fileName;
    }

    private String generateFileName(String originalFileName, UUID id){
        String extension = "";
        if (originalFileName != null && originalFileName.contains("."))
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return String.format("%s%s", id.toString(), extension);
    }

}
