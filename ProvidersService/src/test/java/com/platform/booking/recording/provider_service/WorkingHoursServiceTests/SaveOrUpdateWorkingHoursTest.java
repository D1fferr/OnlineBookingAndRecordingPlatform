package com.platform.booking.recording.provider_service.WorkingHoursServiceTests;

import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursCreateDTO;
import com.platform.booking.recording.provider_service.dtos.WorkingHoursCreateDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.WorkingHoursRepository;
import com.platform.booking.recording.provider_service.services.WorkingHoursService;
import com.platform.booking.recording.provider_service.util.WorkingHoursMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveOrUpdateWorkingHoursTest {

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private WorkingHoursMapper workingHoursMapper;

    @InjectMocks
    private WorkingHoursService workingHoursService;

    @Test
    @DisplayName("saveOrUpdate: Returns immediately when DTO list is empty")
    void saveOrUpdate_EmptyList_ReturnsEarly() {
        // Arrange
        ListWorkingHoursCreateDTO dto = new ListWorkingHoursCreateDTO();
        dto.setWorkingHoursCreateDTOList(List.of());

        // Act
        workingHoursService.saveOrUpdate(dto);

        // Assert
        verify(providerRepository, never()).findById(any());
        verify(workingHoursRepository, never()).findAllByProvider(any());
        verify(workingHoursRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("saveOrUpdate: Throws ProviderNotFoundException when provider does not exist")
    void saveOrUpdate_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        WorkingHoursCreateDTO createDTO = new WorkingHoursCreateDTO();

        ListWorkingHoursCreateDTO listDTO = new ListWorkingHoursCreateDTO();
        listDTO.setProviderId(providerId);
        listDTO.setWorkingHoursCreateDTOList(List.of(createDTO));

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> workingHoursService.saveOrUpdate(listDTO));

        verify(workingHoursRepository, never()).findAllByProvider(any());
        verify(workingHoursRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("saveOrUpdate: Successfully updates existing hours and maps IDs correctly")
    void saveOrUpdate_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        UUID existingHoursId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(providerId);

        WorkingHours existingWorkingHours = new WorkingHours();
        existingWorkingHours.setId(existingHoursId);
        existingWorkingHours.setDayOfWeek(1); // Monday

        WorkingHoursCreateDTO mondayDTO = new WorkingHoursCreateDTO();
        mondayDTO.setDayOfWeek(1);

        ListWorkingHoursCreateDTO listDTO = new ListWorkingHoursCreateDTO();
        listDTO.setProviderId(providerId);
        listDTO.setWorkingHoursCreateDTOList(List.of(mondayDTO));

        WorkingHours mappedEntity = new WorkingHours();
        mappedEntity.setDayOfWeek(1);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(workingHoursRepository.findAllByProvider(provider)).thenReturn(List.of(existingWorkingHours));
        when(workingHoursMapper.createDTOToEntity(mondayDTO, provider)).thenReturn(mappedEntity);

        // Act
        workingHoursService.saveOrUpdate(listDTO);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<WorkingHours>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(workingHoursRepository, times(1)).saveAll(listCaptor.capture());

        List<WorkingHours> savedEntities = listCaptor.getValue();
        assertEquals(1, savedEntities.size());
        assertEquals(existingHoursId, savedEntities.get(0).getId()); // ID successfully retained for UPDATE
    }
}
