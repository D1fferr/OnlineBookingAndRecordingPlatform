package com.platform.booking.recording.AuthSevice.services;

import com.platform.booking.recording.AuthSevice.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthSevice.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthSevice.models.User;
import com.platform.booking.recording.AuthSevice.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaProducerService {
    private final KafkaTemplate<String, UserForKafkaDTO> kafkaTemplate;
    private final Mapper mapper;

    public void send(RegistrationUserDTO dto, User user){
        UserForKafkaDTO userForKafkaDTO = mapper.registrationUserToUserToKafkaDTO(dto, user);
        try {
            kafkaTemplate.send("user-topic", userForKafkaDTO);
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }

    }

}
