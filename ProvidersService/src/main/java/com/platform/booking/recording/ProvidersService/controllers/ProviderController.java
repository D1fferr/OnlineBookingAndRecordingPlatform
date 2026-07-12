package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.services.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderService providerService;

    @PostMapping("/set-working-hours")
    public ResponseEntity<Void> setWorkingHours(@RequestBody @Valid ){

    }
}
