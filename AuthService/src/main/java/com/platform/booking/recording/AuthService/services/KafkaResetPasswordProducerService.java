package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthService.models.ResetPassword;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaResetPasswordProducerService {

    private final KafkaTemplate<String, ResetPassword> userResetPasswordKafkaTemplate;

    public void send(ResetPassword resetPassword){
        try {
            userResetPasswordKafkaTemplate.send("reset-password-topic", resetPassword).get();
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }



}
