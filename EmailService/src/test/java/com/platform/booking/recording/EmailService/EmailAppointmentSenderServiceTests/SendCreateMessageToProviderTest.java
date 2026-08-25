package com.platform.booking.recording.EmailService.EmailAppointmentSenderServiceTests;

import com.platform.booking.recording.EmailService.config.ExternalConfig;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.EmailService.services.EmailAppointmentSenderService;
import com.platform.booking.recording.EmailService.services.TextAppointmentPrepareService;
import com.platform.booking.recording.EmailService.util.MetricsCounter;
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
class SendCreateMessageToProviderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TextAppointmentPrepareService textAppointmentPrepareService;

    @Mock
    private MetricsCounter metricsCounter;

    @Mock
    private ExternalConfig config;

    @Mock
    private ExternalConfig.Mail mail;

    @InjectMocks
    private EmailAppointmentSenderService emailAppointmentSenderService;

    private final String fromEmail = "noreply@booking-platform.com";

    @BeforeEach
    void setUp() {
        when(config.getMail()).thenReturn(mail);
        when(mail.getFrom()).thenReturn(fromEmail);
    }

    @Test
    @DisplayName("sendCreateMessageToProvider: Successfully prepares text, sends creation email to provider and increments metric")
    void sendCreateMessageToProvider_Success() {
        // Arrange
        String providerEmail = "provider@example.com";
        String preparedText = "You have a new appointment booking.";

        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setProviderEmail(providerEmail);
        dto.setClientName("Jane Doe");

        when(textAppointmentPrepareService.prepareCreateMessageToProvider(dto)).thenReturn(preparedText);

        // Act
        emailAppointmentSenderService.sendCreateMessageToProvider(dto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(providerEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("New appointment was created", sentMessage.getSubject());
        assertEquals(preparedText, sentMessage.getText());

        verify(metricsCounter, times(1)).incrementEmailCounter("crete_message_to_provider", "success");
    }

    @Test
    @DisplayName("sendCreateMessageToProvider: Throws exception when JavaMailSender fails")
    void sendCreateMessageToProvider_MailSenderFailure_ThrowsException() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setProviderEmail("provider@example.com");

        when(textAppointmentPrepareService.prepareCreateMessageToProvider(dto)).thenReturn("New appointment notification");
        doThrow(new MailSendException("SMTP connection error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(MailSendException.class, () -> emailAppointmentSenderService.sendCreateMessageToProvider(dto));

        verify(metricsCounter, never()).incrementEmailCounter(anyString(), anyString());
    }
}
