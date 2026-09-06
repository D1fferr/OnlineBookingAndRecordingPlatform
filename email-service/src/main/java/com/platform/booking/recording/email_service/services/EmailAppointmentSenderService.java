package com.platform.booking.recording.email_service.services;

import com.platform.booking.recording.email_service.config.ExternalConfig;
import com.platform.booking.recording.email_service.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.email_service.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.email_service.util.MetricsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAppointmentSenderService {
    private final JavaMailSender mailSender;
    private final TextAppointmentPrepareService textAppointmentPrepareService;
    private final ExternalConfig config;
    private final MetricsCounter metricsCounter;
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCreateMessageToClient(AppointmentCreateDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("New appointment was created");
        message.setText(textAppointmentPrepareService.prepareCreateMessageToClient(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("crete_message_to_client", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log("The create message sent to client");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCreateMessageToProvider(AppointmentCreateDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getProviderEmail());
        message.setSubject("New appointment was created");
        message.setText(textAppointmentPrepareService.prepareCreateMessageToProvider(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("crete_message_to_provider", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log("The create message sent to provider");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendConfirmedMessage(AppointmentCreateDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("Appointment confirmed");
        message.setText(textAppointmentPrepareService.prepareConfirmedMessageToClient(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("confirmed_message_to_client", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log("The confirmed message sent to client");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCancelledMessage(AppointmentCancelledDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("Appointment cancelled");
        message.setText(textAppointmentPrepareService.prepareCancelledMessageToClient(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("cancelled_message_to_client", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log("The cancelled message sent to client");
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendDeletedMessageToProvider(AppointmentCreateDTO dto){
        String FROM = config.getMail().getFrom();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getProviderEmail());
        message.setSubject("Appointment cancelled");
        message.setText(textAppointmentPrepareService.prepareCancelledMessageToProvider(dto));
        mailSender.send(message);
        metricsCounter.incrementEmailCounter("deleted_message_to_provider", "success");
        log.atInfo()
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log("The deleted message sent to provider");
    }


    @Recover
    public void recover(Exception e, AppointmentCreateDTO dto) {
        metricsCounter.incrementEmailCounter("failed_appointment", "failure");
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .log(e.getMessage());
    }
    @Recover
    public void recover(Exception e, AppointmentCancelledDTO dto) {
        metricsCounter.incrementEmailCounter("failed_appointment", "failure");
        log.atWarn()
                .addKeyValue("exception", e.getClass().getSimpleName())
                .addKeyValue("providerEmail", dto.getProviderEmail())
                .addKeyValue("clientName", dto.getClientName())
                .addKeyValue("reason", dto.getReason())
                .log(e.getMessage());
    }

}
