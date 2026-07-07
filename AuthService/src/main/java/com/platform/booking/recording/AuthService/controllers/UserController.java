package com.platform.booking.recording.AuthService.controllers;

import com.platform.booking.recording.AuthService.dtos.AuthResponseDTO;
import com.platform.booking.recording.AuthService.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthService.exceptions.ValidationUserException;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.security.JwtProvider;
import com.platform.booking.recording.AuthService.services.KafkaProducerService;
import com.platform.booking.recording.AuthService.services.RedisService;
import com.platform.booking.recording.AuthService.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    @PostMapping("/public/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegistrationUserDTO dto,
                                                    BindingResult bindingResult){
        checkErrors(bindingResult);
        User user = userService.save(dto);
        kafkaProducerService.send(dto, user);
        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = jwtProvider.generateRefreshToken();
        redisService.saveRefreshToken(user.getId().toString(), refreshToken);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)                             //xss protection
                .secure(false)                              //http
                .path("api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")                         //csrf protection
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(accessToken));

    }





    private void checkErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new ValidationUserException(errorMessage.toString());
        }
    }

}
