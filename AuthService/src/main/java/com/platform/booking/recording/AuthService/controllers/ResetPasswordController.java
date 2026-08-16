package com.platform.booking.recording.AuthService.controllers;

import com.platform.booking.recording.AuthService.dtos.ResetPasswordDTO;
import com.platform.booking.recording.AuthService.dtos.SendCodeDTO;
import com.platform.booking.recording.AuthService.services.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reset-password")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("public/send-code")
    public ResponseEntity<SendCodeDTO> sendCode(@RequestBody @Valid SendCodeDTO dto){
        resetPasswordService.sendCode(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
    @PostMapping("public/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO dto){
        resetPasswordService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

}
