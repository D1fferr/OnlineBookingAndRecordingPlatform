package com.platform.booking.recording.email_service.EmailUserDataSenderServiceTests;

import com.platform.booking.recording.email_service.config.ExternalConfig;
import com.platform.booking.recording.email_service.dtos.ResetPasswordDTO;
import com.platform.booking.recording.email_service.services.EmailUserDataSenderService;
import com.platform.booking.recording.email_service.services.TextUserDataPrepareService;
import com.platform.booking.recording.email_service.util.MetricsCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendResetPasswordCodeTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TextUserDataPrepareService textUserDataPrepareService;

    @Mock
    private MetricsCounter metricsCounter;

    @Mock
    private ExternalConfig config;

    @Mock
    private ExternalConfig.Mail mail;

    @InjectMocks
    private EmailUserDataSenderService emailUserDataSenderService;

    private final String fromEmail = "noreply@booking-platform.com";

    @BeforeEach
    void setUp() {
        when(config.getMail()).thenReturn(mail);
        when(mail.getFrom()).thenReturn(fromEmail);
    }

    @Test
    @DisplayName("sendResetPasswordCode: Successfully prepares text, sends reset password email and increments metric")
    void sendResetPasswordCode_Success() {
        // Arrange
        String recipientEmail = "user@example.com";
        String preparedText = "Your password reset code is: 123456";

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(recipientEmail);
        dto.setCode("123456");

        when(textUserDataPrepareService.prepareTextForResetPassword(dto)).thenReturn(preparedText);

        // Act
        emailUserDataSenderService.sendResetPasswordCode(dto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(recipientEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Reset password", sentMessage.getSubject());
        assertEquals(preparedText, sentMessage.getText());

        verify(metricsCounter, times(1)).incrementEmailCounter("reset_password_message", "success");
    }

    @Test
    @DisplayName("sendResetPasswordCode: Throws exception when JavaMailSender fails")
    void sendResetPasswordCode_MailSenderFailure_ThrowsException() {
        // Arrange
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("user@example.com");
        dto.setCode("123456");

        when(textUserDataPrepareService.prepareTextForResetPassword(dto)).thenReturn("Reset code text");
        doThrow(new MailSendException("SMTP server connection failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(MailSendException.class, () -> emailUserDataSenderService.sendResetPasswordCode(dto));

        verify(metricsCounter, never()).incrementEmailCounter(anyString(), anyString());
    }
}
