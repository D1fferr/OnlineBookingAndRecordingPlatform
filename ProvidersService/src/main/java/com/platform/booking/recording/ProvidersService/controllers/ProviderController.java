package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.ProviderChangeDataDTO;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderService providerService;


    @PatchMapping("/change-profile/{id}")
    public ResponseEntity<Void> changeProfile(@PathVariable(name = "id")UUID id,
                                              @RequestBody ProviderChangeDataDTO dto){
        providerService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
