package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentGetDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentPageDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
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
    @Transactional
    public Appointment changeStatusToConformed(UUID id){
        Appointment appointment = appointmentRepository.findByIdWithProvider(id)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentsStatus.CONFIRMED);
        return appointment;
    }
    @Transactional
    public Appointment changeStatusToCancelled(UUID id){
        Appointment appointment = appointmentRepository.findByIdWithProvider(id)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentsStatus.CANCELLED);
        return appointment;
    }
    @Transactional
    public Appointment deleteByToken(UUID token){
        Appointment appointment = appointmentRepository.findBySecureTokenWithProvider(token)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
        appointmentRepository.delete(appointment);
        return appointment;
    }
    @Transactional(readOnly = true)
    public AppointmentPageDTO findAppointmentsByProvider(UUID id, Pageable pageable){
        Page<Appointment> appointments = appointmentRepository.findAllByProvider_Id(id, pageable);
        AppointmentPageDTO dto = new AppointmentPageDTO();
        dto.setDtoList(appointments
                .stream()
                .map(appointmentMapper::entityToGetDTO)
                .toList());
        dto.setTotalPages(appointments.getTotalPages());
        dto.setTotalElements(appointments.getTotalElements());
        return dto;
    }
    @Transactional(readOnly = true)
    public AppointmentPageDTO findAppointmentsByProvider(String search, UUID id, Pageable pageable){
        String searchPattern = "%" + search.replaceAll("\\s+", "%") + "%";
        Page<Appointment> appointments = appointmentRepository.findAppointments(searchPattern, id, pageable);
        AppointmentPageDTO dto = new AppointmentPageDTO();
        dto.setDtoList(appointments
                .stream()
                .map(appointmentMapper::entityToGetDTO)
                .toList());
        dto.setTotalPages(appointments.getTotalPages());
        dto.setTotalElements(appointments.getTotalElements());
        return dto;
    }
    @Transactional(readOnly = true)
    public AppointmentGetDTO findById(UUID id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
    return appointmentMapper.entityToGetDTO(appointment);
    }


}
