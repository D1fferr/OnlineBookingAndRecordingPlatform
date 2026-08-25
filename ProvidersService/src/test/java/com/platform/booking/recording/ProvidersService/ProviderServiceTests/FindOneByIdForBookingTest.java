package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetBookingRequestDTO;
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
class FindOneByIdForBookingTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("findOneByIdForBooking: Successfully returns mapped DTO when active provider exists")
    void findOneByIdForBooking_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setIsBlocked(Boolean.FALSE);

        ProviderForGetBookingRequestDTO expectedDTO = new ProviderForGetBookingRequestDTO();

        when(providerRepository.findByIdAndIsBlocked(providerId, Boolean.FALSE))
                .thenReturn(Optional.of(provider));
        when(providerMapper.entityToGetBookingRequestDTO(provider))
                .thenReturn(expectedDTO);

        // Act
        ProviderForGetBookingRequestDTO result = providerService.findOneByIdForBooking(providerId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(providerRepository, times(1)).findByIdAndIsBlocked(providerId, Boolean.FALSE);
        verify(providerMapper, times(1)).entityToGetBookingRequestDTO(provider);
    }

    @Test
    @DisplayName("findOneByIdForBooking: Throws ProviderNotFoundException when provider does not exist or is blocked")
    void findOneByIdForBooking_NotFoundOrBlocked_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        when(providerRepository.findByIdAndIsBlocked(providerId, Boolean.FALSE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> providerService.findOneByIdForBooking(providerId));

        verify(providerMapper, never()).entityToGetBookingRequestDTO(any());
    }
}
