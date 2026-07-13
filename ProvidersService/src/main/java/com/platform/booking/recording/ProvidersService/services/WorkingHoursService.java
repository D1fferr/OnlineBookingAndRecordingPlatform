package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ListWorkingHoursCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.WorkingHours;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.repositories.WorkingHoursRepository;
import com.platform.booking.recording.ProvidersService.util.WorkingHoursMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final WorkingHoursMapper workingHoursMapper;
    private final ProviderRepository providerRepository;

    @Transactional
    public void saveOrUpdate(ListWorkingHoursCreateDTO dto){
        if (dto.getWorkingHoursCreateDTOList().isEmpty()){
            return;
        }
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found"));
        List<WorkingHours> existingHours = workingHoursRepository.findAllByProvider(provider);
        Map<Integer, WorkingHours> existingHoursMap = existingHours
                .stream()
                .collect(Collectors.toMap(WorkingHours::getDayOfWeek, (h) -> h));
        List<WorkingHours> entities = dto.getWorkingHoursCreateDTOList()
                .stream()
                .map((e)->{
                    WorkingHours entity = workingHoursMapper.createDTOToEntity(e, provider);
                    WorkingHours dtoHours = existingHoursMap.get(e.getDayOfWeek());
                    if (dtoHours!=null)
                        entity.setId(dtoHours.getId());
                    return entity;
                })
                .toList();
        workingHoursRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public ListWorkingHoursGetDTO findWorkingHours(UUID id){
        ListWorkingHoursGetDTO dto = new ListWorkingHoursGetDTO();
        if (!providerRepository.existsById(id))
                throw new ProviderNotFoundException("Provider not found");
        List<WorkingHours> workingHours = workingHoursRepository.findAllByProviderId(id);
        dto.setWorkingHoursGetDTODTOList(workingHours
                .stream()
                .map(workingHoursMapper::entityToListGetDTO)
                .toList()
        );
        dto.setProviderId(id);
        return dto;
    }

}
