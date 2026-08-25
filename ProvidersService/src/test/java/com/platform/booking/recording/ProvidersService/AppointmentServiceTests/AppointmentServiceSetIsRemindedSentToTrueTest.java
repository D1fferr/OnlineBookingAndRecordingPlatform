package com.platform.booking.recording.ProvidersService.AppointmentServiceTests;

import com.platform.booking.recording.ProvidersService.exceptions.AppointmentNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.repositories.AppointmentRepository;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceSetIsRemindedSentToTrueTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("setIsRemindedSentToTrue: Successfully sets isReminderSent flag to true")
    void setIsRemindedSentToTrue_Success() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setSecureToken(secureToken);
        appointment.setIsReminderSent(Boolean.FALSE);

        when(appointmentRepository.findBySecureTokenWithProvider(secureToken))
                .thenReturn(Optional.of(appointment));

        // Act
        appointmentService.setIsRemindedSentToTrue(secureToken);

        // Assert
        assertTrue(appointment.getIsReminderSent());

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository, times(1)).save(appointmentCaptor.capture());

        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(Boolean.TRUE, savedAppointment.getIsReminderSent());
    }

    @Test
    @DisplayName("setIsRemindedSentToTrue: Throws AppointmentNotFoundException when token is invalid")
    void setIsRemindedSentToTrue_AppointmentNotFound_ThrowsException() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        when(appointmentRepository.findBySecureTokenWithProvider(secureToken))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.setIsRemindedSentToTrue(secureToken));

        verify(appointmentRepository, never()).save(any());
    }
}
