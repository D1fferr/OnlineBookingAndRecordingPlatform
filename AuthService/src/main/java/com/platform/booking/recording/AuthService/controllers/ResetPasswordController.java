package com.platform.booking.recording.AuthService.controllers;

import com.platform.booking.recording.AuthService.dtos.ResetPasswordDTO;
import com.platform.booking.recording.AuthService.dtos.SendCodeDTO;
import com.platform.booking.recording.AuthService.exceptions.ValidationUserException;
import com.platform.booking.recording.AuthService.services.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reset-password")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/send-code")
    public ResponseEntity<SendCodeDTO> sendCode(@RequestBody @Valid SendCodeDTO dto,
                                                BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ValidationUserException(bindingResult.getFieldErrors().toString());
        }
        resetPasswordService.sendCode(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO dto,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ValidationUserException(bindingResult.getFieldErrors().toString());
        }
        resetPasswordService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

}
