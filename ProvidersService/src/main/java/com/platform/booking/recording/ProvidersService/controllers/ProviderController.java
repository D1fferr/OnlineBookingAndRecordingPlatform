package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.ProviderChangeDataDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetClientRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderPageForGetClientRequestDTO;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderService providerService;


    @PatchMapping("/auth/change-profile/{id}")
    public ResponseEntity<Void> changeProfile(@PathVariable(name = "id")UUID id,
                                              @RequestBody ProviderChangeDataDTO dto){
        providerService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/auth/change-avatar/{id}")
    public ResponseEntity<Void> changeAvatar(@PathVariable(name = "id") UUID id,
                                             @RequestPart(name = "imageData") MultipartFile file){
        providerService.updateAvatar(id, file);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/auth/get-one/{id}")
    public ResponseEntity<ProviderForGetRequestDTO> getOneUser(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(providerService.findOneById(id));
    }
    @GetMapping("/public/get-providers")
    public ResponseEntity<ProviderPageForGetClientRequestDTO> getProviders(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                       @RequestParam(value = "providersPerPage", defaultValue = "6", required = false) Integer providersPerPage,
                                                                       @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                       @RequestParam(value = "category", required = false) String category,
                                                                       @RequestParam(value = "search", required = false) String search,
                                                                       @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir){

        var dto = providerService.findProvidersForClient(search, category,
                   PageRequest.of(page, providersPerPage, Sort.Direction.fromString(sortDir), sortBy));
        return ResponseEntity.ok(dto);
    }

}
