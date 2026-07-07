package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public User save(RegistrationUserDTO dto){
        User user = mapper.registrationUserToUser(dto);
        return userRepository.save(user);
    }
}
