package com.platform.booking.recording.email_service.EmailUserDataSenderServiceTests;

import com.platform.booking.recording.email_service.config.ExternalConfig;
import com.platform.booking.recording.email_service.dtos.ProviderCreateDTO;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendRegistrationProviderTest {

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
    @DisplayName("sendRegistrationProvider: Successfully prepares text, sends email and increments metric")
    void sendRegistrationProvider_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        String recipientEmail = "provider@example.com";
        String preparedText = "Welcome to our platform! Your registration is complete.";

        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(providerId);
        dto.setEmail(recipientEmail);

        when(textUserDataPrepareService.prepareTextForRegistration(dto)).thenReturn(preparedText);

        // Act
        emailUserDataSenderService.sendRegistrationProvider(dto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(recipientEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Registration", sentMessage.getSubject());
        assertEquals(preparedText, sentMessage.getText());

        verify(metricsCounter, times(1)).incrementEmailCounter("registration_message", "success");
    }

    @Test
    @DisplayName("sendRegistrationProvider: Throws exception when JavaMailSender fails (allowing Retryable to trigger)")
    void sendRegistrationProvider_MailSenderFailure_ThrowsException() {
        // Arrange
        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("provider@example.com");

        when(textUserDataPrepareService.prepareTextForRegistration(dto)).thenReturn("Welcome!");
        doThrow(new MailSendException("SMTP server unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(MailSendException.class, () -> emailUserDataSenderService.sendRegistrationProvider(dto));

        verify(metricsCounter, never()).incrementEmailCounter(anyString(), anyString());
    }
}
