package com.platform.booking.recording.AuthService.ImageServiceTests;


import com.platform.booking.recording.AuthService.config.ExternalConfig;
import com.platform.booking.recording.AuthService.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreImageTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ExternalConfig config;

    @Mock
    private ExternalConfig.Minio minio;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private ImageService imageService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        when(config.getMinio()).thenReturn(minio);
        when(minio.getBucketName()).thenReturn(bucketName);
    }

    @Test
    @DisplayName("storeImage: Successfully uploads image with extension and returns correct URL")
    void storeImage_Success_WithExtension() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        String originalFilename = "profile-photo.png";
        String contentType = "image/png";
        byte[] contentBytes = "test image content".getBytes();

        when(imageFile.getOriginalFilename()).thenReturn(originalFilename);
        when(imageFile.getContentType()).thenReturn(contentType);
        when(imageFile.getBytes()).thenReturn(contentBytes);

        String expectedFileName = id.toString() + ".png";
        String expectedUrl = "/api/images/" + expectedFileName;

        // Act
        String actualUrl = imageService.storeImage(imageFile, id);

        // Assert
        assertEquals(expectedUrl, actualUrl);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(expectedFileName, capturedRequest.key());
        assertEquals(contentType, capturedRequest.contentType());
    }

    @Test
    @DisplayName("storeImage: Successfully uploads image without file extension")
    void storeImage_Success_WithoutExtension() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        String originalFilename = "avatar";
        String contentType = "image/jpeg";
        byte[] contentBytes = "test bytes".getBytes();

        when(imageFile.getOriginalFilename()).thenReturn(originalFilename);
        when(imageFile.getContentType()).thenReturn(contentType);
        when(imageFile.getBytes()).thenReturn(contentBytes);

        String expectedFileName = id.toString();
        String expectedUrl = "/api/images/" + expectedFileName;

        // Act
        String actualUrl = imageService.storeImage(imageFile, id);

        // Assert
        assertEquals(expectedUrl, actualUrl);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals(expectedFileName, capturedRequest.key());
    }

    @Test
    @DisplayName("storeImage: Throws Exception when reading bytes fails")
    void storeImage_BytesReadError_ThrowsException() throws IOException {
        // Arrange
        UUID id = UUID.randomUUID();
        when(imageFile.getOriginalFilename()).thenReturn("photo.jpg");
        when(imageFile.getContentType()).thenReturn("image/jpeg");
        when(imageFile.getBytes()).thenThrow(new IOException("Disk read error"));

        // Act & Assert
        assertThrows(IOException.class, () -> imageService.storeImage(imageFile, id));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}