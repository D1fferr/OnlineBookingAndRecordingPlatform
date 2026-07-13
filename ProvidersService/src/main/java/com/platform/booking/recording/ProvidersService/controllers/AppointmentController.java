package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.services.KafkaAppointmentCreateProducerService;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final KafkaAppointmentCreateProducerService kafkaAppointmentCreateProducerService;

    @PostMapping("/create")
    public ResponseEntity<Void> createAppointment(@RequestBody @Valid AppointmentCreateDTO dto,
                                                  BindingResult bindingResult){
        checkErrors(bindingResult);
        Appointment appointment = appointmentService.save(dto);
        kafkaAppointmentCreateProducerService.send(appointment, providerService.findEmailById(dto.getProviderId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
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


