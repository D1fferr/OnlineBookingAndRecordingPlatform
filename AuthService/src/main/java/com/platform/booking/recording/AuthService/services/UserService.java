package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.ChangeCredentialsDTO;
import com.platform.booking.recording.AuthService.dtos.LoginDTO;
import com.platform.booking.recording.AuthService.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthService.exceptions.BadCredentialsException;
import com.platform.booking.recording.AuthService.exceptions.FailedSaveImageException;
import com.platform.booking.recording.AuthService.exceptions.UserNotFoundException;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Transactional(noRollbackFor = FailedSaveImageException.class)
    public User save(RegistrationUserDTO dto, MultipartFile file){
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = mapper.registrationUserToUser(dto);
        user.setCreatedAt(OffsetDateTime.now());
        userRepository.saveAndFlush(user);
        if (file!=null){
            try {
                String url = imageService.storeImage(file, user.getId());
                user.setAvatarURL(url);
            } catch (Exception e) {
                throw new FailedSaveImageException(e.getMessage() + e.getCause());
            }
        }
        return userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public User findUserById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found"));
    }
    @Transactional(readOnly = true)
    public User login(LoginDTO dto){
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new BadCredentialsException("Password or email are incorrect"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Password or email are incorrect");
        }
        return user;
    }
    @Transactional
    public User updateUser(UUID id, ChangeCredentialsDTO dto){
        User user = userRepository.findById(id)
                .orElseThrow(()->  new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
            throw new BadCredentialsException("Password are incorrect");
        if (dto.getEmail()!=null)
            user.setEmail(dto.getEmail());
        if (dto.getPassword()!=null)
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }
    @Transactional
    public void updateAvatar(UUID id, MultipartFile file){
        User user = userRepository.findById(id)
                .orElseThrow(()->  new UserNotFoundException("User not found"));
        if (file!=null){
            try {
                String url = imageService.storeImage(file, user.getId());
                user.setAvatarURL(url);
            } catch (Exception e) {
                throw new FailedSaveImageException(e.getMessage() + e.getCause());
            }
        }
        userRepository.save(user);
    }
}
