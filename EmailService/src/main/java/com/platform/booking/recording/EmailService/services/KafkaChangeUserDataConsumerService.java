package com.platform.booking.recording.EmailService.services;


import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaChangeUserDataConsumerService {


    @KafkaListener(topics = "user-topic",
                   containerFactory = "providerRegistrationKafkaListenerContainerFactory")
    public void getProvider(ProviderCreateDTO dto){
        providerService.save(dto);
    }

}
