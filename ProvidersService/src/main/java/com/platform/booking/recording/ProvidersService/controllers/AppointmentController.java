package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledReasonDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentGetDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentPageDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.services.KafkaAppointmentProducerService;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @PostMapping("/create")
    public ResponseEntity<Void> createAppointment(@RequestBody @Valid AppointmentCreateDTO dto,
                                                  BindingResult bindingResult){
        checkErrors(bindingResult);
        Appointment appointment = appointmentService.save(dto);
        kafkaAppointmentProducerService.sendToCreate(appointment, appointment.getProvider().getEmail(), appointment.getProvider().getTimezone());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("change-status-to-confirmed/{id}")
    public ResponseEntity<Void> changeStatusToConfirmed(@PathVariable(name = "id") UUID id){
        Appointment appointment = appointmentService.changeStatusToConformed(id);
        kafkaAppointmentProducerService.sendToConfirmed(appointment, appointment.getProvider().getEmail(), appointment.getProvider().getTimezone());
        return ResponseEntity.ok().build();
    }
    @PostMapping("change-status-to-cancelled/{id}")
    public ResponseEntity<Void> changeStatusToCancelled(@PathVariable(name = "id") UUID id,
                                                        @RequestBody @Valid AppointmentCancelledReasonDTO reason,
                                                        BindingResult bindingResult){
        if (bindingResult.hasErrors())
            throw new ValidationException(bindingResult.getFieldErrors().toString());
        Appointment appointment = appointmentService.changeStatusToCancelled(id);
        kafkaAppointmentProducerService.sendToCancelled(appointment, appointment.getProvider().getEmail(), appointment.getProvider().getTimezone(), reason.getReason());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-appointment-by-user/{secure-token}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable(name = "secure-token") UUID token){
        Appointment appointment = appointmentService.deleteByToken(token);
        kafkaAppointmentProducerService.sendToDeleted(appointment, appointment.getProvider().getEmail(), appointment.getProvider().getTimezone());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/get-appointments-by-provider/{id}")
    public ResponseEntity<AppointmentPageDTO> getAppointmentsByProvider(@PathVariable(name = "id") UUID id,
                                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(value = "appPerPage", defaultValue = "8", required = false) Integer appPerPage,
                                                                        @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                        @RequestParam(value = "search", required = false) String search,
                                                                        @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir){
        AppointmentPageDTO pageDTO;
        if (search!=null) {
            pageDTO = appointmentService.findAppointmentsByProvider(search, id, PageRequest.of(page, appPerPage, Sort.Direction.fromString(sortDir), sortBy));
        }else {
            pageDTO = appointmentService.findAppointmentsByProvider(id, PageRequest.of(page, appPerPage, Sort.Direction.fromString(sortDir), sortBy));
        }
        return ResponseEntity.ok(pageDTO);
    }
    @GetMapping("/get-appointment/{id}")
    public ResponseEntity<AppointmentGetDTO> getAppointment(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    private void checkErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new ValidationException(errorMessage.toString());
        }
    }
}


