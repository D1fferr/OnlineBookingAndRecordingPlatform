package com.platform.booking.recording.provider_service.controllers;

import com.platform.booking.recording.provider_service.dtos.ServiceCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ServicePageDTO;
import com.platform.booking.recording.provider_service.dtos.ServiceUpdateDTO;
import com.platform.booking.recording.provider_service.services.ServiceProviderService;
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
    private final ServiceProviderService serviceProviderService;

    @PostMapping("/auth/create")
    public ResponseEntity<Void> createService(@RequestBody @Valid ServiceCreateDTO dto){
        serviceProviderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PatchMapping("/auth/update/{id}")
    public ResponseEntity<Void> updateService(@PathVariable(name = "id") UUID id,
                                              @RequestBody @Valid ServiceUpdateDTO dto){
        serviceProviderService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/auth/get-services/{id}")
    public ResponseEntity<ServicePageDTO> getServices(@PathVariable(name = "id") UUID id,
                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "servicePerPage", defaultValue = "8", required = false) Integer servicePerPage,
                                                          @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir){
        Pageable pageable = PageRequest.of(page, servicePerPage, Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(serviceProviderService.findServices(id, pageable));
    }
    @DeleteMapping("/auth/delete/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable (name = "id") UUID id){
        serviceProviderService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
