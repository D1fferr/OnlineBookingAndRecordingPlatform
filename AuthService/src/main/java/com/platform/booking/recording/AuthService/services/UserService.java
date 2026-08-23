package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.*;
import com.platform.booking.recording.AuthService.exceptions.*;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(noRollbackFor = FailedSaveImageException.class)
    public User save(RegistrationUserDTO dto, MultipartFile file){
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new UserAlreadyExistException("User with this email already exist");
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = mapper.registrationUserToUser(dto);
        user.setCreatedAt(OffsetDateTime.now());
        user.setIsBlocked(Boolean.FALSE);
        user.setRole("ROLE_PROVIDER");
        userRepository.saveAndFlush(user);
        eventPublisher.publishEvent(mapper.registrationUserToUserToKafkaDTO(dto, user));
        log.atInfo()
                .addKeyValue("userId", user.getId())
                .log("The user saved");
        return storeImage(file, user);
    }
    @Transactional(noRollbackFor = FailedSaveImageException.class)
    public User saveAsAdmin(RegistrationUserDTO dto, MultipartFile file){
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new UserAlreadyExistException("User with this email already exist");
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = mapper.registrationUserToUser(dto);
        user.setCreatedAt(OffsetDateTime.now());
        user.setIsBlocked(Boolean.FALSE);
        user.setRole("ROLE_ADMIN");
        userRepository.saveAndFlush(user);
        eventPublisher.publishEvent(mapper.registrationUserToUserToKafkaDTO(dto, user));
        log.atInfo()
                .addKeyValue("userId", user.getId())
                .log("The user saved as admin");
        return storeImage(file, user);
    }
    private User storeImage(MultipartFile file, User user) {
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
    @Transactional
    public void saveAfterResetPassword(User user){
        userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public User findUserById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found"));
    }
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
    @Transactional(readOnly = true)
    public User login(LoginDTO dto){
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new BadCredentialsException("Password or email are incorrect"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Password or email are incorrect");
        }
        if (user.getIsBlocked())
            throw new UserIsBlockedException(user.getBlockReason());
        log.atInfo()
                .addKeyValue("userId", user.getId())
                .log("Login successful");
        return user;
    }
    @Transactional
    public User updateUser(UUID id, ChangeCredentialsDTO dto){
        User user = userRepository.findById(id)
                .orElseThrow(()->  new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))
            throw new BadCredentialsException("Password are incorrect");
        if (dto.getEmail()!=null){
            user.setEmail(dto.getEmail());
            eventPublisher.publishEvent(new ProviderUpdateEmailDTO(id, dto.getEmail()));
        }
        if (dto.getPassword()!=null)
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        log.atInfo()
                .addKeyValue("userId", user.getId())
                .log("The user updated");
        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(UUID id){
        userRepository.deleteById(id);
        log.atInfo()
                .addKeyValue("userId", id)
                .log("The user deleted");
    }
    @Transactional
    public void blockUser(BlockUserDTO dto){
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(()->new UserNotFoundException("User not found"));
        user.setIsBlocked(Boolean.TRUE);
        user.setBlockReason(dto.getReason());
        log.atInfo()
                .addKeyValue("userId", user.getId())
                .addKeyValue("reason", user.getBlockReason())
                .log("The user blocked");
        userRepository.save(user);
        eventPublisher.publishEvent(new ProviderIsBlockedDTO(user.getId(), user.getIsBlocked()));
        eventPublisher.publishEvent(new UserIdDTO(user.getId()));

    }
    @Transactional(readOnly = true)
    public PageUserDTO findAllUsers(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        PageUserDTO dto = new PageUserDTO();
        dto.setDtos(users.getContent().stream().map(mapper::userToGetDTO).toList());
        dto.setTotalPages(users.getTotalPages());
        dto.setTotalElements(users.getTotalElements());
        return dto;
    }
    @Transactional(readOnly = true)
    public PageUserDTO findAllUsersWithSearch(String search, Pageable pageable){
        String searchPattern = "%" + search.replaceAll("\\s+", "%") + "%";
        Page<User> users = userRepository.findUsers(searchPattern, pageable);
        PageUserDTO dto = new PageUserDTO();
        dto.setDtos(users.getContent().stream().map(mapper::userToGetDTO).toList());
        dto.setTotalPages(users.getTotalPages());
        dto.setTotalElements(users.getTotalElements());
        return dto;
    }
    @Transactional(readOnly = true)
    public UserForGetRequestDTO findOneById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        return mapper.userToGetDTO(user);
    }

    @Transactional
    public void unblockUser(UUID id){
        MDC.put("userId", id.toString());
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        user.setIsBlocked(Boolean.FALSE);
        userRepository.save(user);
        eventPublisher.publishEvent(new ProviderIsBlockedDTO(user.getId(), user.getIsBlocked()));
        log.atInfo().log("The user unblocked");
    }
    @Transactional
    public void updateAvatar(UUID id, MultipartFile file){
        MDC.put("providerId", id.toString());
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
        eventPublisher.publishEvent(new UserAvatarForKafkaDTO(user.getId(), user.getAvatarURL()));
        log.atInfo().log("The avatar was updated");

    }
}
