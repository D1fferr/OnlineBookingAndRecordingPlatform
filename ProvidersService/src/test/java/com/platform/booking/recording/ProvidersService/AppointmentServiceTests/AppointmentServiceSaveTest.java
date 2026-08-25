package com.platform.booking.recording.ProvidersService.AppointmentServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentGetForCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderAndServiceProjection;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentConflictException;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.exceptions.ServiceProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.AppointmentsStatus;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import com.platform.booking.recording.ProvidersService.repositories.AppointmentRepository;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceSaveTest {
    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("save: Successfully creating a booking with correct fields and publishing the event")
    void save_Success() {
        // GIVEN
        UUID providerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        OffsetDateTime startTime = OffsetDateTime.now().plusDays(1);
        OffsetDateTime endTime = startTime.plusHours(1);

        AppointmentCreateDTO createDTO = new AppointmentCreateDTO();
        createDTO.setProviderId(providerId);
        createDTO.setServiceId(serviceId);
        createDTO.setStartTime(startTime);
        createDTO.setEndTime(endTime);

        Provider provider = new Provider();
        provider.setId(providerId);

        ServiceProvider service = new ServiceProvider();
        service.setId(serviceId);

        ProviderAndServiceProjection projection = mock(ProviderAndServiceProjection.class);
        when(projection.getProvider()).thenReturn(provider);
        when(projection.getService()).thenReturn(service);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());

        AppointmentGetForCreateDTO expectedResponseDTO = new AppointmentGetForCreateDTO();
        AppointmentForKafkaDTO kafkaDTO = new AppointmentCreateForKafkaDTO();

        when(providerRepository.findByIdWithLock(providerId, serviceId)).thenReturn(Optional.of(projection));
        when(appointmentRepository.existsOverlappingAppointment(providerId, startTime, endTime)).thenReturn(false);
        when(appointmentMapper.createDTOtoEntity(createDTO, provider)).thenReturn(appointment);
        when(appointmentMapper.entityToGetForCreateDTO(appointment, service)).thenReturn(expectedResponseDTO);
        when(appointmentMapper.entityToCreateForKafkaDTO(appointment)).thenReturn(kafkaDTO);

        // WHEN
        AppointmentGetForCreateDTO result = appointmentService.save(createDTO);

        // THEN
        assertNotNull(result);
        assertEquals(expectedResponseDTO, result);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository, times(1)).save(appointmentCaptor.capture());

        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(AppointmentsStatus.PENDING, savedAppointment.getStatus());
        assertEquals(Boolean.FALSE, savedAppointment.getIsReminderSent());
        assertNotNull(savedAppointment.getSecureToken());
        assertNotNull(savedAppointment.getCreatedAt());

        verify(eventPublisher, times(1)).publishEvent(kafkaDTO);
    }

    @Test
    @DisplayName("save: Throws ProviderNotFoundException if provider is not found")
    void save_ProviderNotFound_ThrowsException() {
        // GIVEN
        AppointmentCreateDTO createDTO = createBaseDTO();
        when(providerRepository.findByIdWithLock(createDTO.getProviderId(), createDTO.getServiceId()))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ProviderNotFoundException.class, () -> appointmentService.save(createDTO));

        verify(appointmentRepository, never()).existsOverlappingAppointment(any(), any(), any());
        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("save: Throws Service ProviderNotFoundException if the service provider does not have it.")
    void save_ServiceNotFound_ThrowsException() {
        // GIVEN
        AppointmentCreateDTO createDTO = createBaseDTO();
        ProviderAndServiceProjection projection = mock(ProviderAndServiceProjection.class);

        when(projection.getService()).thenReturn(null);
        when(providerRepository.findByIdWithLock(createDTO.getProviderId(), createDTO.getServiceId()))
                .thenReturn(Optional.of(projection));

        // WHEN & THEN
        assertThrows(ServiceProviderNotFoundException.class, () -> appointmentService.save(createDTO));

        verify(appointmentRepository, never()).existsOverlappingAppointment(any(), any(), any());
        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("save: Викидає AppointmentConflictException, якщо час уже зайнятий")
    void save_OverlappingAppointment_ThrowsException() {
        // GIVEN
        AppointmentCreateDTO createDTO = createBaseDTO();
        ProviderAndServiceProjection projection = mock(ProviderAndServiceProjection.class);
        when(projection.getService()).thenReturn(new ServiceProvider());

        when(providerRepository.findByIdWithLock(createDTO.getProviderId(), createDTO.getServiceId()))
                .thenReturn(Optional.of(projection));
        when(appointmentRepository.existsOverlappingAppointment(createDTO.getProviderId(), createDTO.getStartTime(), createDTO.getEndTime()))
                .thenReturn(true);

        // WHEN & THEN
        assertThrows(AppointmentConflictException.class, () -> appointmentService.save(createDTO));

        verify(appointmentMapper, never()).createDTOtoEntity(any(), any());
        verify(appointmentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private AppointmentCreateDTO createBaseDTO() {
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setProviderId(UUID.randomUUID());
        dto.setServiceId(UUID.randomUUID());
        dto.setStartTime(OffsetDateTime.now().plusDays(1));
        dto.setEndTime(dto.getStartTime().plusHours(1));
        return dto;
    }
}
