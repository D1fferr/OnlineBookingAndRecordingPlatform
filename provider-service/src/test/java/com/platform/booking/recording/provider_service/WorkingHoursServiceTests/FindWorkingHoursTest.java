package com.platform.booking.recording.provider_service.WorkingHoursServiceTests;

import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.provider_service.dtos.WorkingHoursGetDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.WorkingHoursRepository;
import com.platform.booking.recording.provider_service.services.WorkingHoursService;
import com.platform.booking.recording.provider_service.util.WorkingHoursMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindWorkingHoursTest {

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private WorkingHoursMapper workingHoursMapper;

    @InjectMocks
    private WorkingHoursService workingHoursService;

    @Test
    @DisplayName("findWorkingHours: Successfully returns mapped working hours DTO for existing provider")
    void findWorkingHours_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();

        WorkingHours workingHours = new WorkingHours();
        workingHours.setId(UUID.randomUUID());
        workingHours.setDayOfWeek(1);

        WorkingHoursGetDTO getDTO = new WorkingHoursGetDTO();

        when(providerRepository.existsById(providerId)).thenReturn(true);
        when(workingHoursRepository.findAllByProvider_Id(providerId)).thenReturn(List.of(workingHours));
        when(workingHoursMapper.entityToListGetDTO(workingHours)).thenReturn(getDTO);

        // Act
        ListWorkingHoursGetDTO result = workingHoursService.findWorkingHours(providerId);

        // Assert
        assertNotNull(result);
        assertEquals(providerId, result.getProviderId());
        assertEquals(1, result.getWorkingHoursGetDTODTOList().size());
        assertEquals(getDTO, result.getWorkingHoursGetDTODTOList().get(0));

        verify(providerRepository, times(1)).existsById(providerId);
        verify(workingHoursRepository, times(1)).findAllByProvider_Id(providerId);
        verify(workingHoursMapper, times(1)).entityToListGetDTO(workingHours);
    }

    @Test
    @DisplayName("findWorkingHours: Throws ProviderNotFoundException when provider does not exist")
    void findWorkingHours_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        when(providerRepository.existsById(providerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ProviderNotFoundException.class,
                () -> workingHoursService.findWorkingHours(providerId));

        verify(workingHoursRepository, never()).findAllByProvider_Id(any());
        verify(workingHoursMapper, never()).entityToListGetDTO(any());
    }
}
