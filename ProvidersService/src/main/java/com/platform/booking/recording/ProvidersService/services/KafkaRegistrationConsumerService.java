package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderUpdateEmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaRegistrationConsumerService {

    private final ProviderService providerService;

    @KafkaListener(topics = "user-topic",
                   containerFactory = "providerRegistrationKafkaListenerContainerFactory")
    public void getProvider(ProviderCreateDTO dto){
        providerService.save(dto);
    }
    @KafkaListener(topics = "user-email-topic",
            containerFactory = "providerEmailKafkaListenerContainerFactory")
    public void getProvider(ProviderUpdateEmailDTO dto){
        providerService.updateEmail(dto);
    }
}
