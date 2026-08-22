package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.*;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentGetAndSendToKafkaDTO;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.services.KafkaAppointmentProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @PostMapping("/public/create")
    public ResponseEntity<AppointmentGetForCreateDTO> createAppointment(@RequestBody @Valid AppointmentCreateDTO dto){
        AppointmentGetForCreateDTO appointmentDTO = appointmentService.save(dto);
        return ResponseEntity.ok(appointmentDTO);
    }
    @PostMapping("/auth/change-status-to-confirmed/{id}")
    public ResponseEntity<Void> changeStatusToConfirmed(@PathVariable(name = "id") UUID id){
         appointmentService.changeStatusToConfirmed(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/auth/change-status-to-cancelled/{id}")
    public ResponseEntity<Void> changeStatusToCancelled(@PathVariable(name = "id") UUID id,
                                                        @RequestBody @Valid AppointmentCancelledReasonDTO reason){
        appointmentService.changeStatusToCancelled(id, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/cancel-appointment/{secure-token}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable(name = "secure-token") UUID token){
        appointmentService.deleteByToken(token);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/auth/get-appointments-by-provider/{id}")
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
    @GetMapping("/auth/get-appointment/{id}")
    public ResponseEntity<AppointmentGetDTO> getAppointment(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(appointmentService.findById(id));
    }
    @PostMapping("/public/get-free-slots/{id}")
    public ResponseEntity<AvailableSlotsResponseDTO> getFreeSlots(@PathVariable(name = "id") UUID serviceId){
        return ResponseEntity.ok(appointmentService.findFreeSlots(serviceId));
    }


}


