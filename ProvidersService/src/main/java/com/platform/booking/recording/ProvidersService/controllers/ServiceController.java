package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.ServiceCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ServicePageDTO;
import com.platform.booking.recording.ProvidersService.dtos.ServiceUpdateDTO;
import com.platform.booking.recording.ProvidersService.services.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
public class ServiceController {
    private final ServiceService serviceService;

    @PostMapping("/auth/create")
    public ResponseEntity<Void> createService(@RequestBody @Valid ServiceCreateDTO dto){
        serviceService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PatchMapping("/auth/update/{id}")
    public ResponseEntity<Void> updateService(@PathVariable(name = "id") UUID id,
                                              @RequestBody @Valid ServiceUpdateDTO dto){
        serviceService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/auth/get-services/{id}")
    public ResponseEntity<ServicePageDTO> getServices(@PathVariable(name = "id") UUID id,
                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "servicePerPage", defaultValue = "8", required = false) Integer servicePerPage,
                                                          @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir){
        Pageable pageable = PageRequest.of(page, servicePerPage, Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(serviceService.findServices(id, pageable));
    }
    @DeleteMapping("/auth/delete/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable (name = "id") UUID id){
        serviceService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
