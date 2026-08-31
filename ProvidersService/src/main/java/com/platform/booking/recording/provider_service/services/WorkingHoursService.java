package com.platform.booking.recording.provider_service.services;

import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ListWorkingHoursGetDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.WorkingHoursRepository;
import com.platform.booking.recording.provider_service.util.WorkingHoursMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        MDC.put("providerId", dto.getProviderId().toString());
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
        log.atInfo().log("Working hours were updated");
    }

    @Transactional(readOnly = true)
    public ListWorkingHoursGetDTO findWorkingHours(UUID id){
        MDC.put("providerId", id.toString());
        ListWorkingHoursGetDTO dto = new ListWorkingHoursGetDTO();
        if (!providerRepository.existsById(id))
                throw new ProviderNotFoundException("Provider not found");
        List<WorkingHours> workingHours = workingHoursRepository.findAllByProvider_Id(id);
        dto.setWorkingHoursGetDTODTOList(workingHours
                .stream()
                .map(workingHoursMapper::entityToListGetDTO)
                .toList()
        );
        dto.setProviderId(id);
        return dto;
    }

}
