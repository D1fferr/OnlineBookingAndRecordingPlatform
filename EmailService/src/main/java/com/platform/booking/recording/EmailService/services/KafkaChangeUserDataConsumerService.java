package com.platform.booking.recording.EmailService.services;


import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaChangeUserDataConsumerService {
    private final EmailUserDataSenderService emailUserDataSenderService;

    @KafkaListener(topics = "user-topic",
                   containerFactory = "providerRegistrationKafkaListenerContainerFactory")
    public void getProvider(ProviderCreateDTO dto){
        emailUserDataSenderService.sendRegistrationProvider(dto);
    }
    @KafkaListener(topics = "reset-password-topic",
            containerFactory = "providerResetPasswordKafkaListenerContainerFactory")
    public void getResetPassword(ResetPasswordDTO dto){
        emailUserDataSenderService.sendResetPasswordCode(dto);
    }

}
