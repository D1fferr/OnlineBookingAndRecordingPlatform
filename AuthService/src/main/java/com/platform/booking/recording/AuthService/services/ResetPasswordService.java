package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.ResetPasswordDTO;
import com.platform.booking.recording.AuthService.dtos.SendCodeDTO;
import com.platform.booking.recording.AuthService.exceptions.IncorrectResetCodeException;
import com.platform.booking.recording.AuthService.models.ResetPassword;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.redis.ResetPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResetPasswordService {

    private final ResetPasswordRepository resetPasswordRepository;
    private final UserService userService;
    private final KafkaResetPasswordProducerService kafkaResetPasswordProducerService;
    private final PasswordEncoder passwordEncoder;

    public void sendCode(SendCodeDTO dto){
        Optional<User> user = userService.findUserByEmail(dto.getEmail());
        if (user.isEmpty())
            return;
        String code = generateCode();
        ResetPassword resetPassword = new ResetPassword(dto.getEmail(), code, 900L);
        resetPasswordRepository.save(resetPassword);
        kafkaResetPasswordProducerService.send(resetPassword);
    }
    public void resetPassword(ResetPasswordDTO dto){
        Optional<User> optionalUser = userService.findUserByEmail(dto.getEmail());
        if (optionalUser.isEmpty())
            return;
        if (!validateCode(dto))
            throw new IncorrectResetCodeException("Incorrect reset code or it has expired");
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.saveAfterResetPassword(user);
    }

    private boolean validateCode(ResetPasswordDTO dto){
        Optional<ResetPassword> resetPassword = resetPasswordRepository.findById(dto.getEmail());
        if (resetPassword.isEmpty())
            return false;
        resetPasswordRepository.deleteById(dto.getEmail());
        return resetPassword.get().getCode().equals(dto.getCode());
    }

    private String generateCode(){
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(999999));
    }
}
