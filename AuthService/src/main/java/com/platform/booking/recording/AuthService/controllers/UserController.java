package com.platform.booking.recording.AuthService.controllers;

import com.platform.booking.recording.AuthService.dtos.*;
import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.security.TokenProvider;
import com.platform.booking.recording.AuthService.services.KafkaRegistrationProducerService;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import com.platform.booking.recording.AuthService.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final KafkaRegistrationProducerService kafkaRegistrationProducerService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;

    @PostMapping("/public/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestPart(name = "userData")  @Valid RegistrationUserDTO dto,
                                                    @RequestPart(name = "imageData", required = false) MultipartFile file){
        User user = userService.save(dto, file);
        kafkaRegistrationProducerService.send(dto, user);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));

    }
    @PostMapping("/public/register/as-admin")
    public ResponseEntity<AuthResponseDTO> registerAsAdmin(@RequestPart(name = "userData")  @Valid RegistrationUserDTO dto,
                                                           @RequestPart(name = "imageData", required = false) MultipartFile file){
        User user = userService.saveAsAdmin(dto, file);
        kafkaRegistrationProducerService.send(dto, user);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));

    }
    @PostMapping("/public/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@CookieValue(name = "refreshToken") String refreshToken){
        TokenResponse newTokens = tokenProvider.refreshTokens(refreshToken);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(newTokens.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(newTokens.getAccessToken()));
    }

    @PostMapping("/public/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO){
        User user = userService.login(loginDTO);
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));
    }
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken") String refreshToken){
        if (refreshToken!=null){
            Optional<RefreshToken> refreshRedisToken = refreshTokenService.findByRefreshTokenForLogout(refreshToken);
            refreshRedisToken.ifPresent(refreshTokenService::delete);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, tokenProvider.createClearShareCookie().toString())
                .build();
    }
    @PostMapping("/auth/change-credentials/{id}")
    public ResponseEntity<AuthResponseDTO> changeCredentials(@PathVariable(name = "id") UUID id,
                                                             @RequestBody @Valid ChangeCredentialsDTO dto){
        User user = userService.updateUser(id, dto);
        if (dto.getEmail()!=null)
            kafkaRegistrationProducerService.sendEmail(user.getId(), user.getEmail());
        TokenResponse tokenResponse = tokenProvider.createTokens(user);
        ResponseCookie responseCookie = tokenProvider.createResponseCookie(tokenResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new AuthResponseDTO(tokenResponse.getAccessToken()));
    }
    @PostMapping("/auth/change-avatar/{id}")
    public ResponseEntity<Void> changeAvatar(@PathVariable(name = "id") UUID id,
                                             @RequestPart(name = "imageData") MultipartFile file){
        userService.updateAvatar(id, file);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/auth/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/auth/block-user")
    public ResponseEntity<Void> blockUser(@RequestBody @Valid BlockUserDTO dto){
        userService.blockUser(dto);
        refreshTokenService.deleteByUserId(dto.getUserId());
        return ResponseEntity.noContent()
                .build();
    }
    @PostMapping("/auth/logout-user/{id}")
    public ResponseEntity<Void> logoutUser(@PathVariable(name = "id") UUID id){
        refreshTokenService.deleteByUserId(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/auth/get-all-users")
    public ResponseEntity<PageUserDTO> getAllUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "usersPerPage", defaultValue = "8", required = false) Integer usersPerPage,
                                                   @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(value = "search", required = false) String search,
                                                   @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir){
        PageUserDTO dto;
        Pageable pageable = PageRequest.of(page, usersPerPage, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        if (search!=null){
            dto = userService.findAllUsersWithSearch(search, pageable);
        }else {
            dto = userService.findAllUsers(pageable);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/auth/get-one-user/{id}")
    public ResponseEntity<UserForGetRequestDTO> getOneUser(@PathVariable(name = "id") UUID id){
        return ResponseEntity.ok(userService.findOneById(id));
    }


}
