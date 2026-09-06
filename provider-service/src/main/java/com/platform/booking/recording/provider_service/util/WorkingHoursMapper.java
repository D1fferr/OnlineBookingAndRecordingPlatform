package com.platform.booking.recording.provider_service.util;

import com.platform.booking.recording.provider_service.dtos.WorkingHoursCreateDTO;
import com.platform.booking.recording.provider_service.dtos.WorkingHoursGetDTO;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import org.springframework.stereotype.Component;

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
