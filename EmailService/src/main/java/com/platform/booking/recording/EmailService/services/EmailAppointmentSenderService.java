package com.platform.booking.recording.EmailService.services;

import com.platform.booking.recording.EmailService.config.ExternalConfig;
import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAppointmentSenderService {
    private final JavaMailSender mailSender;
    private final TextAppointmentPrepareService textAppointmentPrepareService;
    private final ExternalConfig config;
    private final String FROM = config.getMail().getFrom();

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCreateMessageToClient(AppointmentCreateDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("New appointment was created");
        message.setText(textAppointmentPrepareService.prepareCreateMessageToClient(dto));
        mailSender.send(message);
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCreateMessageToProvider(AppointmentCreateDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getProviderEmail());
        message.setSubject("New appointment was created");
        message.setText(textAppointmentPrepareService.prepareCreateMessageToProvider(dto));
        mailSender.send(message);
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendConfirmedMessage(AppointmentCreateDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("Appointment confirmed");
        message.setText(textAppointmentPrepareService.prepareConfirmedMessageToClient(dto));
        mailSender.send(message);
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendCancelledMessage(AppointmentCancelledDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getClientEmail());
        message.setSubject("Appointment cancelled");
        message.setText(textAppointmentPrepareService.prepareCancelledMessageToClient(dto));
        mailSender.send(message);
    }
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendDeletedMessageToProvider(AppointmentCreateDTO dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(dto.getProviderEmail());
        message.setSubject("Appointment cancelled");
        message.setText(textAppointmentPrepareService.prepareCancelledMessageToProvider(dto));
        mailSender.send(message);
    }


    @Recover
    public void recover(Exception e, AppointmentCreateDTO dto) {
        //add logging
    }
    @Recover
    public void recover(Exception e, AppointmentCancelledDTO dto) {
        //add logging
    }

}
