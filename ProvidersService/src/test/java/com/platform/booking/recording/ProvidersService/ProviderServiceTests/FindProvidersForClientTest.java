package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetClientRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderPageForGetClientRequestDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import com.platform.booking.recording.ProvidersService.util.ProviderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindProvidersForClientTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("findProvidersForClient: Successfully fetches and sorts mapped providers for client")
    void findProvidersForClient_Success() {
        // Arrange
        String rawSearch = "john doe";
        String expectedSearchPattern = "%john%doe%";
        String category = "HAIRCUT";
        Pageable pageable = PageRequest.of(0, 10);

        UUID providerId = UUID.randomUUID();
        Page<UUID> idPage = new PageImpl<>(List.of(providerId), pageable, 1);

        Provider provider = new Provider();
        provider.setId(providerId);

        ProviderForGetClientRequestDTO clientDTO = new ProviderForGetClientRequestDTO();

        when(providerRepository.findProviderIds(eq(expectedSearchPattern), eq(category), eq(pageable)))
                .thenReturn(idPage);
        when(providerRepository.findAllByIdsIn(List.of(providerId)))
                .thenReturn(List.of(provider));
        when(providerMapper.entityToGetForClientRequestDTO(provider))
                .thenReturn(clientDTO);

        // Act
        ProviderPageForGetClientRequestDTO result = providerService.findProvidersForClient(rawSearch, category, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getDtos().size());
        assertEquals(clientDTO, result.getDtos().get(0));

        verify(providerRepository, times(1)).findProviderIds(expectedSearchPattern, category, pageable);
        verify(providerRepository, times(1)).findAllByIdsIn(List.of(providerId));
        verify(providerMapper, times(1)).entityToGetForClientRequestDTO(provider);
    }

    @Test
    @DisplayName("findProvidersForClient: Handles null search query correctly")
    void findProvidersForClient_NullSearch_Success() {
        // Arrange
        String category = "HAIRCUT";
        Pageable pageable = PageRequest.of(0, 10);

        UUID providerId = UUID.randomUUID();
        Page<UUID> idPage = new PageImpl<>(List.of(providerId), pageable, 1);

        Provider provider = new Provider();
        provider.setId(providerId);

        when(providerRepository.findProviderIds(isNull(), eq(category), eq(pageable)))
                .thenReturn(idPage);
        when(providerRepository.findAllByIdsIn(List.of(providerId)))
                .thenReturn(List.of(provider));
        when(providerMapper.entityToGetForClientRequestDTO(provider))
                .thenReturn(new ProviderForGetClientRequestDTO());

        // Act
        ProviderPageForGetClientRequestDTO result = providerService.findProvidersForClient(null, category, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getDtos().size());
        verify(providerRepository, times(1)).findProviderIds(isNull(), eq(category), eq(pageable));
    }

    @Test
    @DisplayName("findProvidersForClient: Returns empty page DTO when no provider IDs found")
    void findProvidersForClient_EmptyPage() {
        // Arrange
        String search = "nonexistent";
        String expectedSearchPattern = "%nonexistent%";
        String category = "HAIRCUT";
        Pageable pageable = PageRequest.of(0, 10);

        Page<UUID> emptyIdPage = Page.empty(pageable);

        when(providerRepository.findProviderIds(eq(expectedSearchPattern), eq(category), eq(pageable)))
                .thenReturn(emptyIdPage);

        // Act
        ProviderPageForGetClientRequestDTO result = providerService.findProvidersForClient(search, category, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getDtos().isEmpty());

        verify(providerRepository, never()).findAllByIdsIn(any());
        verify(providerMapper, never()).entityToGetForClientRequestDTO(any());
    }
}