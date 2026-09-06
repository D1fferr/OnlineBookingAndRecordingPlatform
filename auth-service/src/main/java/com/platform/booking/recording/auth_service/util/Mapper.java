package com.platform.booking.recording.auth_service.util;

import com.platform.booking.recording.auth_service.dtos.RegistrationUserDTO;
import com.platform.booking.recording.auth_service.dtos.UserForGetRequestDTO;
import com.platform.booking.recording.auth_service.dtos.UserForKafkaDTO;
import com.platform.booking.recording.auth_service.models.User;
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
    public UserForGetRequestDTO userToGetDTO (User user){
        UserForGetRequestDTO dto = new UserForGetRequestDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatarURL(user.getAvatarURL());
        dto.setIsBlocked(user.getIsBlocked());
        dto.setBlockReason(user.getBlockReason());
        return dto;
    }


}
