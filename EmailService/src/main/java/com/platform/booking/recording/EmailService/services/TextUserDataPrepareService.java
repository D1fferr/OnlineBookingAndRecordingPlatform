package com.platform.booking.recording.EmailService.services;

import com.platform.booking.recording.EmailService.config.ExternalConfig;
import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextUserDataPrepareService {
    private final ExternalConfig config;

    public String prepareTextForRegistration(ProviderCreateDTO dto){
        String HOST = config.getServices().getGateway();
        return "Hello dear " + dto.getName() + """
                We are glad to welcome you to the Online Booking and Recording Platform team! Now you have a convenient tool for managing your schedule, recording clients, and developing your own business.
                To get started and attract your first clients:
                    Fill out your profile: add photos of your work so that clients can see your style.
                    Set up your schedule: specify the days and hours when you are convenient to accept appointments.
                    Add services: list your procedures and indicate current prices."""
                + "\n Go to profile settings" + HOST + "/profile/" + dto.getId() + "\n If you have any questions about the settings, our support service is always there." + """ 
                        Sincerely,
                        The Online Booking and Recording Platform team""";

    }
    public String prepareTextForResetPassword(ResetPasswordDTO dto){
        return "Password recovery verification code: " + dto.getCode() +
                ". Do not share this code with anyone unless you requested a password recovery, simply ignore this message.";
    }
}
