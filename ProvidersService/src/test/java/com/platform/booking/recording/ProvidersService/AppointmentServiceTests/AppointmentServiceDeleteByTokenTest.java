package com.platform.booking.recording.ProvidersService.AppointmentServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentDeletedForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Appointment;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceDeleteByTokenTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("deleteByToken: Successfully deletes appointment and publishes Kafka event")
    void deleteByToken_Success() {
        // Arrange
        UUID token = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setSecureToken(token);

        AppointmentForKafkaDTO kafkaDTO = new AppointmentDeletedForKafkaDTO();

        when(appointmentRepository.findBySecureTokenWithProvider(token))
                .thenReturn(Optional.of(appointment));
        when(appointmentMapper.entityToDeletedForKafkaDTO(appointment))
                .thenReturn(kafkaDTO);

        // Act
        appointmentService.deleteByToken(token);

        // Assert
        verify(appointmentRepository, times(1)).delete(appointment);
        verify(eventPublisher, times(1)).publishEvent(kafkaDTO);
    }

    @Test
    @DisplayName("deleteByToken: Throws AppointmentNotFoundException when token is invalid")
    void deleteByToken_AppointmentNotFound_ThrowsException() {
        // Arrange
        UUID token = UUID.randomUUID();
        when(appointmentRepository.findBySecureTokenWithProvider(token))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.deleteByToken(token));

        verify(appointmentRepository, never()).delete(any());
        verify(appointmentMapper, never()).entityToDeletedForKafkaDTO(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
