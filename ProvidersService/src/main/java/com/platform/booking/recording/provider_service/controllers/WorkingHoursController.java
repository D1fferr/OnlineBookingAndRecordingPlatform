package com.platform.booking.recording.provider_service.controllers;


import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.provider_service.services.WorkingHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/working-hours")
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @PostMapping("/auth/set-working-hours")
    public ResponseEntity<Void> setWorkingHours(@RequestBody @Valid ListWorkingHoursCreateDTO dto){

        workingHoursService.saveOrUpdate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/public/get-working-hours/{id}")
    public ResponseEntity<ListWorkingHoursGetDTO> getWorkingHours(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(workingHoursService.findWorkingHours(id));
    }




}
