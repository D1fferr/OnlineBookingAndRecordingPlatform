package com.platform.booking.recording.ProvidersService.controllers;

import com.platform.booking.recording.ProvidersService.dtos.*;
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

    @GetMapping("/auth/get-one/{id}")
    public ResponseEntity<ProviderForGetRequestDTO> getOneUser(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(providerService.findOneById(id));
    }
    @GetMapping("/public/get-providers")
    public ResponseEntity<ProviderPageForGetClientRequestDTO> getProviders(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                       @RequestParam(value = "providersPerPage", defaultValue = "6", required = false) Integer providersPerPage,
                                                                       @RequestParam(value = "category", required = false) String category,
                                                                       @RequestParam(value = "search", required = false) String search){

        var dto = providerService.findProvidersForClient(search, category, PageRequest.of(page, providersPerPage));
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/public/get-categories")
    public ResponseEntity<ProviderListServiceTypeDTO> getCategories(){
        return ResponseEntity.ok(providerService.finAllCategories());
    }
    @GetMapping("/public/get-one/{id}")
    public ResponseEntity<ProviderForGetBookingRequestDTO> getOneUserForClient(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(providerService.findOneByIdForBooking(id));
    }

}
