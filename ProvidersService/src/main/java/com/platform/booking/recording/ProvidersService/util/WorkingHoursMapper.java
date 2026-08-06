package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.ProvidersService.dtos.WorkingHoursCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.WorkingHoursGetDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.WorkingHours;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.repositories.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkingHoursMapper {


    public WorkingHours createDTOToEntity(WorkingHoursCreateDTO dto, Provider provider){
        WorkingHours workingHours = new WorkingHours();
        workingHours.setProvider(provider);
        workingHours.setStartTime(dto.getStartTime());
        workingHours.setEndTime(dto.getEndTime());
        workingHours.setBreakStartTime(dto.getBreakStartTime());
        workingHours.setBreakEndTime(dto.getBreakEndTime());
        workingHours.setDayOfWeek(dto.getDayOfWeek());
        workingHours.setIsActive(dto.getIsActive());
        workingHours.setSlotStep(dto.getSlotStep());
        return workingHours;
    }
    public WorkingHoursGetDTO entityToListGetDTO(WorkingHours workingHours){
        WorkingHoursGetDTO dto = new WorkingHoursGetDTO();
        dto.setId(workingHours.getId());
        dto.setStartTime(workingHours.getStartTime());
        dto.setEndTime(workingHours.getEndTime());
        dto.setBreakStartTime(workingHours.getBreakStartTime());
        dto.setBreakEndTime(workingHours.getBreakEndTime());
        dto.setDayOfWeek(workingHours.getDayOfWeek());
        dto.setIsActive(workingHours.getIsActive());
        dto.setSlotStep(workingHours.getSlotStep());
        return dto;
    }
}
