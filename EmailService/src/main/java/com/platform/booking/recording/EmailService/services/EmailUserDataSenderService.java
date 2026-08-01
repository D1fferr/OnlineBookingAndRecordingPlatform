package com.platform.booking.recording.EmailService.services;

import com.platform.booking.recording.EmailService.config.ExternalConfig;
import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailUserDataSenderService {
    private final JavaMailSender mailSender;
    private final TextUserDataPrepareService textUserDataPrepareService;
    private final ExternalConfig config;
    private final String FROM = config.getMail().getFrom();

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendRegistrationProvider(ProviderCreateDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getEmail());
        message.setSubject("Registration");
        message.setText(textUserDataPrepareService.prepareTextForRegistration(dto));
        mailSender.send(message);
        log.atInfo()
                .addKeyValue("providerId", dto.getId())
                .log("The registration message sent to provider");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendResetPasswordCode(ResetPasswordDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getEmail());
        message.setSubject("Reset password");
        message.setText(textUserDataPrepareService.prepareTextForResetPassword(dto));
        mailSender.send(message);
        log.atInfo()
                .addKeyValue("providerEmail", dto.getEmail())
                .log("The reset password message sent to provider");
    }

    @Recover
    public void recover(Exception e, ProviderCreateDTO dto) {
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerId", dto.getId())
                .log(e.getMessage());
    }
    @Recover
    public void recover(Exception e, ResetPasswordDTO dto) {
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerEmail", dto.getEmail())
                .log(e.getMessage());
    }
}
