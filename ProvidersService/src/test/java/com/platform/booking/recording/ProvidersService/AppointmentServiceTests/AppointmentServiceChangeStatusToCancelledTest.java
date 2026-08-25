package com.platform.booking.recording.ProvidersService.AppointmentServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledReasonDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCancelledForKafkaDTO;
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
public class AppointmentServiceChangeStatusToCancelledTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private AppointmentService appointmentService;
    @Test
    @DisplayName("changeStatusToCancelled: Successfully changes status to CANCELLED and publishes event")
    void changeStatusToCancelled_success(){
        //Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentsStatus.PENDING);
        AppointmentCancelledReasonDTO cancelledReasonDTO = new AppointmentCancelledReasonDTO();
        cancelledReasonDTO.setReason("reason");
        AppointmentCancelledForKafkaDTO kafkaDTO = new AppointmentCancelledForKafkaDTO();

        when(appointmentRepository.findByIdWithProvider(appointmentId))
                .thenReturn(Optional.of(appointment));
        when(appointmentMapper.entityToCancelledForKafkaDTO(appointment, cancelledReasonDTO.getReason()))
                .thenReturn(kafkaDTO);
        //Act
        appointmentService.changeStatusToCancelled(appointmentId, cancelledReasonDTO);
        // Assert
        assertEquals(AppointmentsStatus.CANCELLED, appointment.getStatus());
        verify(eventPublisher, times(1)).publishEvent(kafkaDTO);
    }
    @Test
    @DisplayName("changeStatusToCancelled: Throws AppointmentNotFoundException when appointment does not exist")
    void changeStatusToCancelled_AppointmentNotFound_ThrowsException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        AppointmentCancelledReasonDTO cancelledReasonDTO = new AppointmentCancelledReasonDTO();
        when(appointmentRepository.findByIdWithProvider(appointmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.changeStatusToCancelled(appointmentId, cancelledReasonDTO));

        verify(appointmentMapper, never()).entityConfirmedToForKafkaDTO(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
