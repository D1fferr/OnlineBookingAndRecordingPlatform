package com.platform.booking.recording.provider_service.ProviderServiceTests;

import com.platform.booking.recording.provider_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.services.ProviderService;
import com.platform.booking.recording.provider_service.util.ProviderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveProviderTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("save: Successfully maps DTO, sets default fields, and saves provider")
    void save_Success() {
        // Arrange
        ProviderCreateDTO createDTO = new ProviderCreateDTO();
        Provider provider = new Provider();
        provider.setId(UUID.randomUUID());

        when(providerMapper.createDTOToProvider(createDTO)).thenReturn(provider);

        // Act
        providerService.save(createDTO);

        // Assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository, times(1)).save(providerCaptor.capture());

        Provider savedProvider = providerCaptor.getValue();
        assertEquals(Boolean.FALSE, savedProvider.getIsBlocked());
        assertNotNull(savedProvider.getCreatedAt());
        verify(providerMapper, times(1)).createDTOToProvider(createDTO);
    }
}
