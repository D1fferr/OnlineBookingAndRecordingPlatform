package com.platform.booking.recording.AuthService.controllers;

import com.platform.booking.recording.AuthService.dtos.*;
import com.platform.booking.recording.AuthService.exceptions.ValidationUserException;
import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.security.JwtProvider;
import com.platform.booking.recording.AuthService.security.TokenProvider;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    @PostMapping("/public/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestPart(name = "userData")  @Valid RegistrationUserDTO dto,
                                                    @RequestPart(name = "imageData", required = false) MultipartFile file,
                                                    BindingResult bindingResult){
        checkErrors(bindingResult);
        User user = userService.save(dto, file);
        kafkaProducerService.send(dto, user);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));

    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@CookieValue(name = "refreshToken") String refreshToken){
        TokenResponse newTokens = tokenProvider.refreshTokens(refreshToken);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(newTokens.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(newTokens.getAccessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO){
        User user = userService.login(loginDTO);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken") String refreshToken){
        if (refreshToken!=null){
            Optional<RefreshToken> refreshRedisToken = redisService.findByRefreshTokenForLogout(refreshToken);
            refreshRedisToken.ifPresent(redisService::delete);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, tokenProvider.createClearShareCookie().toString())
                .build();
    }
    @PostMapping("/change-credentials/{id}")
    public ResponseEntity<AuthResponseDTO> changeCredentials(@PathVariable(name = "id") UUID id,
                                                             @RequestBody ChangeCredentialsDTO dto,
                                                             BindingResult bindingResult){
        checkErrors(bindingResult);
        User user = userService.updateUser(id, dto);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));
    }
    @PostMapping("/change-avatar/{id}")
    public ResponseEntity<Void> changeAvatar(@PathVariable(name = "id") UUID id,
                                             @RequestPart(name = "imageData") MultipartFile file){
        userService.updateAvatar(id, file);
        return ResponseEntity.ok().build();
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
