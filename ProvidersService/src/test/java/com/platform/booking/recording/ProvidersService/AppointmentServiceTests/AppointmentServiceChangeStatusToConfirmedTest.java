package com.platform.booking.recording.ProvidersService.AppointmentServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentConfirmedForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.AppointmentsStatus;
import com.platform.booking.recording.ProvidersService.repositories.AppointmentRepository;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceChangeStatusToConfirmedTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("changeStatusToConfirmed: Successfully changes status to CONFIRMED and publishes event")
    void changeStatusToConfirmed_Success() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentsStatus.PENDING);

        AppointmentForKafkaDTO kafkaDTO = new AppointmentConfirmedForKafkaDTO();

        when(appointmentRepository.findByIdWithProvider(appointmentId))
                .thenReturn(Optional.of(appointment));
        when(appointmentMapper.entityConfirmedToForKafkaDTO(appointment))
                .thenReturn(kafkaDTO);

        // Act
        appointmentService.changeStatusToConfirmed(appointmentId);

        // Assert
        assertEquals(AppointmentsStatus.CONFIRMED, appointment.getStatus());
        verify(eventPublisher, times(1)).publishEvent(kafkaDTO);
    }

    @Test
    @DisplayName("changeStatusToConfirmed: Throws AppointmentNotFoundException when appointment does not exist")
    void changeStatusToConfirmed_AppointmentNotFound_ThrowsException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findByIdWithProvider(appointmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.changeStatusToConfirmed(appointmentId));

        verify(appointmentMapper, never()).entityConfirmedToForKafkaDTO(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
