package com.platform.booking.recording.email_service.KafkaChangeUserDataConsumerServiceTests;

import com.platform.booking.recording.email_service.dtos.ResetPasswordDTO;
import com.platform.booking.recording.email_service.services.EmailUserDataSenderService;
import com.platform.booking.recording.email_service.services.KafkaChangeUserDataConsumerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetResetPasswordTest {

    @Mock
    private EmailUserDataSenderService emailUserDataSenderService;

    @InjectMocks
    private KafkaChangeUserDataConsumerService kafkaChangeUserDataConsumerService;

    @Test
    @DisplayName("getResetPassword: Successfully consumes DTO from reset-password-topic and delegates to emailUserDataSenderService")
    void getResetPassword_Success() {
        // Arrange
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("user@example.com");
        dto.setCode("123456");

        // Act
        kafkaChangeUserDataConsumerService.getResetPassword(dto);

        // Assert
        verify(emailUserDataSenderService, times(1)).sendResetPasswordCode(dto);
    }
}
