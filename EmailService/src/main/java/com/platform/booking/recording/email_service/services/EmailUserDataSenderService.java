package com.platform.booking.recording.email_service.services;

import com.platform.booking.recording.email_service.config.ExternalConfig;
import com.platform.booking.recording.email_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.email_service.dtos.ResetPasswordDTO;
import com.platform.booking.recording.email_service.util.MetricsCounter;
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
    private final MetricsCounter metricsCounter;
    private final ExternalConfig config;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendRegistrationProvider(ProviderCreateDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getEmail());
        message.setSubject("Registration");
        message.setText(textUserDataPrepareService.prepareTextForRegistration(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("registration_message", "success");
        log.atInfo()
                .addKeyValue("providerId", dto.getId())
                .log("The registration message sent to provider");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendResetPasswordCode(ResetPasswordDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getEmail());
        message.setSubject("Reset password");
        message.setText(textUserDataPrepareService.prepareTextForResetPassword(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("reset_password_message", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getEmail())
                .log("The reset password message sent to provider");
    }

    @Recover
    public void recover(Exception e, ProviderCreateDTO dto) {
        metricsCounter.incrementEmailCounter("failed_registration_message", "failure");
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerId", dto.getId())
                .log(e.getMessage());
    }
    @Recover
    public void recover(Exception e, ResetPasswordDTO dto) {
        metricsCounter.incrementEmailCounter("failed_reset_password_message", "failure");
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerEmail", dto.getEmail())
                .log(e.getMessage());
    }
}
