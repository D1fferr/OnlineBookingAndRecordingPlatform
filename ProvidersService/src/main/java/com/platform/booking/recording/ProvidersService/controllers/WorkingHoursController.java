package com.platform.booking.recording.ProvidersService.controllers;


import com.platform.booking.recording.ProvidersService.dtos.ListWorkingHoursCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.ProvidersService.services.WorkingHoursService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
