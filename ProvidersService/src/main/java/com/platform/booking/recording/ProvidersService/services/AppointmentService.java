package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentConflictException;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentNotFoundException;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.AppointmentsStatus;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.AppointmentRepository;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ProviderRepository providerRepository;

    @Transactional
    public Appointment save(AppointmentCreateDTO dto){
        if (!providerRepository.existsById(dto.getProviderId()))
            throw new ProviderNotFoundException("Provider not found");
        if(appointmentRepository.existsOverlappingAppointment(dto.getProviderId(), dto.getStartTime(), dto.getEndTime()))
            throw new AppointmentConflictException("This time is already taken by another client!");
        Provider provider = providerRepository.getReferenceById(dto.getProviderId());
        Appointment appointment = appointmentMapper.createDTOtoEntity(dto, provider);
        appointment.setCreatedAt(OffsetDateTime.now());
        appointment.setIsReminderSent(Boolean.FALSE);
        appointment.setSecureToken(UUID.randomUUID());
        appointment.setStatus(AppointmentsStatus.PENDING);
        return appointmentRepository.save(appointment);
    }
    @Transactional
    public void setIsRemindedSentToTrue(UUID id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
        appointment.setIsReminderSent(Boolean.TRUE);
        appointmentRepository.save(appointment);
    }


}
