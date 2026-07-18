package com.platform.booking.recording.ProvidersService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final ExternalConfig config;

    @Bean
    public S3Client s3Client(){
        String minioEndpoint = config.getMinio().getEndpoint();
        String accessKey = config.getMinio().getAccessKey();
        String secretKey = config.getMinio().getSecretKey();
        return S3Client.builder()
                .endpointOverride(URI.create(minioEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

}
