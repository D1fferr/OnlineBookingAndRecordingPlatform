package com.platform.booking.recording.provider_service.services;

import com.platform.booking.recording.provider_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.provider_service.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaRegistrationConsumerService {

    private final ProviderService providerService;

    @KafkaListener(topics = "user-topic",
                   containerFactory = "providerRegistrationKafkaListenerContainerFactory")
    public void getProvider(@Payload ProviderCreateDTO dto){
        providerService.save(dto);
    }
    @KafkaListener(topics = "user-email-topic",
            containerFactory = "providerEmailKafkaListenerContainerFactory")
    public void getProvider(@Payload ProviderUpdateEmailDTO dto){
        providerService.updateEmail(dto);
    }
    @KafkaListener(topics = "user-is-blocked-topic",
            containerFactory = "providerIsBlockedKafkaListenerContainerFactory")
    public void getProvider(@Payload ProviderIsBlockedDTO dto){
        providerService.updateIsBlocked(dto);
    }
    @KafkaListener(topics = "user-avatar-topic",
            containerFactory = "providerAvatarKafkaListenerContainerFactory")
    public void getProvider(@Payload UserAvatarForKafkaDTO dto){
        providerService.updateAvatar(dto);
    }

}
