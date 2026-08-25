package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderListServiceTypeDTO;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllCategoriesTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("finAllCategories: Successfully returns DTO containing list of service types")
    void finAllCategories_Success() {
        // Arrange
        List<String> serviceTypes = List.of("HAIRCUT", "MASSAGE", "MANICURE");
        when(providerRepository.findAllUniqueServiceTypes()).thenReturn(serviceTypes);

        // Act
        ProviderListServiceTypeDTO result = providerService.finAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getCategories().size());
        assertEquals(serviceTypes, result.getCategories());
        verify(providerRepository, times(1)).findAllUniqueServiceTypes();
    }

    @Test
    @DisplayName("finAllCategories: Returns DTO with empty list when no service types exist")
    void finAllCategories_EmptyList() {
        // Arrange
        when(providerRepository.findAllUniqueServiceTypes()).thenReturn(List.of());

        // Act
        ProviderListServiceTypeDTO result = providerService.finAllCategories();

        // Assert
        assertNotNull(result);
        assertTrue(result.getCategories().isEmpty());
        verify(providerRepository, times(1)).findAllUniqueServiceTypes();
    }
}