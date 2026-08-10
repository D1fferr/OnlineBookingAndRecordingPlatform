package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.*;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentConflictException;
import com.platform.booking.recording.ProvidersService.exceptions.AppointmentNotFoundException;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.exceptions.ServiceProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.*;
import com.platform.booking.recording.ProvidersService.repositories.AppointmentRepository;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.repositories.ServiceRepository;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public AppointmentGetAndSendToKafkaDTO save(AppointmentCreateDTO dto) {
        MDC.put("providerId", dto.getProviderId().toString());
        MDC.put("startTime", dto.getStartTime().toString());
        MDC.put("endTime", dto.getEndTime().toString());
        Optional<ProviderAndServiceProjection> projection = providerRepository.findByIdWithLock(dto.getProviderId(), dto.getServiceId());
        if (projection.isEmpty())
            throw new ProviderNotFoundException("Provider not found");
        if (projection.get().getService()==null)
            throw new ServiceProviderNotFoundException("Service not found");
        if (appointmentRepository.existsOverlappingAppointment(dto.getProviderId(), dto.getStartTime(), dto.getEndTime()))
            throw new AppointmentConflictException("This time is already taken by another client!");
        Appointment appointment = appointmentMapper.createDTOtoEntity(dto, projection.get().getProvider());
        appointment.setCreatedAt(OffsetDateTime.now());
        appointment.setIsReminderSent(Boolean.FALSE);
        appointment.setSecureToken(UUID.randomUUID());
        appointment.setStatus(AppointmentsStatus.PENDING);
        appointmentRepository.save(appointment);
        AppointmentGetAndSendToKafkaDTO getAndSendToKafkaDTO = new AppointmentGetAndSendToKafkaDTO();
        getAndSendToKafkaDTO.setDto(appointmentMapper.entityToGetForCreateDTO(appointment, projection.get().getProvider(), projection.get().getService()));
        getAndSendToKafkaDTO.setAppointment(appointment);
        log.atInfo()
                .addKeyValue("appointmentId", appointment.getId())
                .log("The appointment was created");
        return getAndSendToKafkaDTO;
    }

    @Transactional
    public void setIsRemindedSentToTrue(UUID id) {
        MDC.put("appointmentId", id.toString());
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        appointment.setIsReminderSent(Boolean.TRUE);
        appointmentRepository.save(appointment);
        log.atInfo().log("The appointment is reminded sent was changed to true");

    }

    @Transactional
    public Appointment changeStatusToConformed(UUID id) {
        MDC.put("appointmentId", id.toString());
        Appointment appointment = appointmentRepository.findByIdWithProvider(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentsStatus.CONFIRMED);
        log.atInfo().log("The appointment status was changed to confirmed");
        return appointment;
    }

    @Transactional
    public Appointment changeStatusToCancelled(UUID id) {
        MDC.put("appointmentId", id.toString());
        Appointment appointment = appointmentRepository.findByIdWithProvider(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentsStatus.CANCELLED);
        log.atInfo().log("The appointment status was changed to cancelled");

        return appointment;
    }

    @Transactional
    public Appointment deleteByToken(UUID token) {
        MDC.put("secureToken", token.toString());
        Appointment appointment = appointmentRepository.findBySecureTokenWithProvider(token)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        appointmentRepository.delete(appointment);
        log.atInfo().log("The appointment was deleted");
        return appointment;
    }

    @Transactional(readOnly = true)
    public AppointmentPageDTO findAppointmentsByProvider(UUID id, Pageable pageable) {
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
    public AppointmentPageDTO findAppointmentsByProvider(String search, UUID id, Pageable pageable) {
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
    public AppointmentGetDTO findById(UUID id) {
        MDC.put("appointmentId", id.toString());
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        return appointmentMapper.entityToGetDTO(appointment);
    }

    @Transactional(readOnly = true)
    public AvailableSlotsResponseDTO findFreeSlots(UUID id) {
        ServiceProvider serviceProvider = serviceRepository.findWithProviderAndWorkingHoursById(id)
                .orElseThrow(() -> new ServiceProviderNotFoundException("Service not found"));
        List<Appointment> appointments = providerRepository.findBookedAppointmentsForPeriod(serviceProvider.getProvider().getId(),
                OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        Integer duration = serviceProvider.getDuration();
        List<WorkingHours> workingHours = serviceProvider.getProvider().getWorkingHours();
        AvailableSlotsResponseDTO availableSlotsResponseDTO = new AvailableSlotsResponseDTO();
        List<DaySlotsDTO> daySlotsDTOList = new ArrayList<>();
        for (WorkingHours w : workingHours) {
            if (Boolean.FALSE.equals(w.getIsActive()))
                continue;

            DaySlotsDTO daySlotsDTO = new DaySlotsDTO();
            List<FreeSlotDTO> freeSlots = new ArrayList<>();
            daySlotsDTO.setDayOfWeek(w.getDayOfWeek());

            OffsetDateTime slotStart = OffsetDateTime.from(w.getStartTime());
            OffsetDateTime dayEnd = OffsetDateTime.from(w.getEndTime());

            while (slotStart.plusMinutes(duration).isBefore(dayEnd) || slotStart.plusMinutes(duration).isEqual(dayEnd)) {
                OffsetDateTime slotEnd = slotStart.plusMinutes(duration);
                boolean isLunchBreak = false;
                if (w.getBreakStartTime() != null && w.getBreakEndTime() != null) {
                    OffsetDateTime breakStart = OffsetDateTime.from(w.getBreakStartTime());
                    OffsetDateTime breakEnd = OffsetDateTime.from(w.getBreakEndTime());
                    if (slotStart.isBefore(breakEnd) && slotEnd.isAfter(breakStart)) {
                        isLunchBreak = true;
                    }
                }
                OffsetDateTime finalSlotStart = slotStart;
                boolean isAlreadyBooked = appointments.stream()
                        .filter(a -> a.getStartTime().getDayOfWeek().getValue() == w.getDayOfWeek())
                        .anyMatch(a -> {
                            OffsetDateTime bookedStart = a.getStartTime();
                            OffsetDateTime bookedEnd = a.getEndTime();
                            return finalSlotStart.isBefore(bookedEnd) && slotEnd.isAfter(bookedStart);
                        });

                if (!isLunchBreak && !isAlreadyBooked) {
                    FreeSlotDTO freeSlotDTO = new FreeSlotDTO();
                    freeSlotDTO.setStartTime(slotStart);
                    freeSlotDTO.setEndTime(slotEnd);
                    freeSlots.add(freeSlotDTO);
                }

                int step = (w.getSlotStep() != null && w.getSlotStep() > 0) ? w.getSlotStep() : duration;
                slotStart = slotStart.plusMinutes(step);
            }

            daySlotsDTO.setFreeSlots(freeSlots);
            daySlotsDTOList.add(daySlotsDTO);
        }
        availableSlotsResponseDTO.setAppointments(daySlotsDTOList);
        availableSlotsResponseDTO.setTimezone(serviceProvider.getProvider().getTimezone());
        return availableSlotsResponseDTO;
    }

}
