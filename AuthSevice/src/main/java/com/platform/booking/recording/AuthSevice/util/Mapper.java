package com.platform.booking.recording.AuthSevice.util;

import com.platform.booking.recording.AuthSevice.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthSevice.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthSevice.models.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public UserForKafkaDTO registrationUserToUserToKafkaDTO(RegistrationUserDTO dto, User user){
        UserForKafkaDTO userForKafkaDTO = new UserForKafkaDTO();
        userForKafkaDTO.setId(user.getId());
        userForKafkaDTO.setName(dto.getName());
        userForKafkaDTO.setEmail(dto.getEmail());
        userForKafkaDTO.setServiceType(dto.getServiceType());
        userForKafkaDTO.setTimezone(dto.getTimezone());
        userForKafkaDTO.setAvatarURL(user.getAvatarURL());
        return userForKafkaDTO;
    }
    public User registrationUserToUser(RegistrationUserDTO dto){
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }


}
