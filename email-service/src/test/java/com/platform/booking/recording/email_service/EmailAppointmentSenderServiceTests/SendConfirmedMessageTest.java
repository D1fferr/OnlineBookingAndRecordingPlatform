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
class SendConfirmedMessageTest {

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
    @DisplayName("sendConfirmedMessage: Successfully prepares text, sends confirmation email to client and increments metric")
    void sendConfirmedMessage_Success() {
        // Arrange
        String clientEmail = "client@example.com";
        String preparedText = "Your appointment has been confirmed by the provider.";

        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail(clientEmail);
        dto.setClientName("John Doe");
        dto.setProviderEmail("provider@example.com");

        when(textAppointmentPrepareService.prepareConfirmedMessageToClient(dto)).thenReturn(preparedText);

        // Act
        emailAppointmentSenderService.sendConfirmedMessage(dto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(clientEmail, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Appointment confirmed", sentMessage.getSubject());
        assertEquals(preparedText, sentMessage.getText());

        verify(metricsCounter, times(1)).incrementEmailCounter("confirmed_message_to_client", "success");
    }

    @Test
    @DisplayName("sendConfirmedMessage: Throws exception when JavaMailSender fails")
    void sendConfirmedMessage_MailSenderFailure_ThrowsException() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setClientEmail("client@example.com");

        when(textAppointmentPrepareService.prepareConfirmedMessageToClient(dto)).thenReturn("Confirmed text");
        doThrow(new MailSendException("SMTP connection error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(MailSendException.class, () -> emailAppointmentSenderService.sendConfirmedMessage(dto));

        verify(metricsCounter, never()).incrementEmailCounter(anyString(), anyString());
    }
}