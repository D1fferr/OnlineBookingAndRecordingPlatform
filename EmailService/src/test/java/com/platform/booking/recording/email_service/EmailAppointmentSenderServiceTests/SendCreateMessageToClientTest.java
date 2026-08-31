package com.platform.booking.recording.email_service.EmailAppointmentSenderServiceTests;

import com.platform.booking.recording.email_service.config.ExternalConfig;
import com.platform.booking.recording.email_service.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.email_service.services.EmailAppointmentSenderService;
import com.platform.booking.recording.email_service.services.TextAppointmentPrepareService;
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
class SendCreateMessageToClientTest {

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
    @DisplayName("sendCreateMessageToClient: Successfully prepares text, sends creation email to client and increments metric")
    void sendCreateMessageToClient_Success() {
        // Arrange
        String clientEmail = "client@example.com";
        String preparedText = "Your appointment has been successfully created.";

        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail(clientEmail);
        dto.setClientName("John Doe");
        dto.setProviderEmail("provider@example.com");

        when(textAppointmentPrepareService.prepareCreateMessageToClient(dto)).thenReturn(preparedText);

        // Act
        emailAppointmentSenderService.sendCreateMessageToClient(dto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(clientEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("New appointment was created", sentMessage.getSubject());
        assertEquals(preparedText, sentMessage.getText());

        verify(metricsCounter, times(1)).incrementEmailCounter("crete_message_to_client", "success");
    }

    @Test
    @DisplayName("sendCreateMessageToClient: Throws exception when JavaMailSender fails")
    void sendCreateMessageToClient_MailSenderFailure_ThrowsException() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setClientEmail("client@example.com");

        when(textAppointmentPrepareService.prepareCreateMessageToClient(dto)).thenReturn("Appointment created");
        doThrow(new MailSendException("SMTP server connection failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(MailSendException.class, () -> emailAppointmentSenderService.sendCreateMessageToClient(dto));

        verify(metricsCounter, never()).incrementEmailCounter(anyString(), anyString());
    }
}
