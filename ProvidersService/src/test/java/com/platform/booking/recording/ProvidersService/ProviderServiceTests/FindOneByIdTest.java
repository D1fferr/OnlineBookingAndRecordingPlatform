package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetRequestDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOneByIdTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("findOneById: Successfully returns mapped DTO when provider exists")
    void findOneById_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        Provider provider = new Provider();
        provider.setId(providerId);

        ProviderForGetRequestDTO expectedDTO = new ProviderForGetRequestDTO();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(providerMapper.entityToGetRequestDTO(provider)).thenReturn(expectedDTO);

        // Act
        ProviderForGetRequestDTO result = providerService.findOneById(providerId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerMapper, times(1)).entityToGetRequestDTO(provider);
    }

    @Test
    @DisplayName("findOneById: Throws ProviderNotFoundException when provider does not exist")
    void findOneById_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> providerService.findOneById(providerId));

        verify(providerMapper, never()).entityToGetRequestDTO(any());
    }
}
