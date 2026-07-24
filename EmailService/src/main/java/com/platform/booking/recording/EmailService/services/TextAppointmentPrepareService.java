package com.platform.booking.recording.EmailService.services;

import com.platform.booking.recording.EmailService.config.ExternalConfig;
import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
@RequiredArgsConstructor
public class TextAppointmentPrepareService {
    private final ExternalConfig config;
    private final String HOST = config.getServices().getGateway();
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    public String prepareCreateMessageToClient(AppointmentCreateDTO dto){
        String cancel = HOST + "/cancel-appointment/" + dto.getSecureToken();
        ZoneId providerZone = ZoneId.of(dto.getTimezone());
        OffsetDateTime startTime = dto.getStartTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        OffsetDateTime endTime = dto.getEndTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        return "Hello dear " + dto.getClientName() +
                "\n We are glad that you have chosen our service. As soon as the master confirms your appointment, you will receive a notification by email.\n" +
                "To cancel an appointment, you can follow this link: \n" + cancel +
                "\n Appointment details: " +
                "\n Appointment starts at " + startTime.format(FORMATTER) +
                "\n Appointment ends at " + endTime.format(FORMATTER) +
                "\n Comment: " + dto.getClientComment();
    }
    public String prepareCreateMessageToProvider(AppointmentCreateDTO dto){
        ZoneId providerZone = ZoneId.of(dto.getTimezone());
        OffsetDateTime startTime = dto.getStartTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        OffsetDateTime endTime = dto.getEndTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        return "You have a new Appointment!" +
                "\n Appointment details: " +
                "\n Client : " + dto.getClientName() +
                "\n Appointment starts at " + startTime.format(FORMATTER) +
                "\n Appointment ends at " + endTime.format(FORMATTER) +
                "\n Comment: " + dto.getClientComment();
    }
    public String prepareConfirmedMessageToClient(AppointmentCreateDTO dto){
        ZoneId providerZone = ZoneId.of(dto.getTimezone());
        OffsetDateTime startTime = dto.getStartTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        OffsetDateTime endTime = dto.getEndTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        return "Your appointment was confirmed" +
                "\n Appointment details: " +
                "\n Appointment starts at " + startTime.format(FORMATTER) +
                "\n Appointment ends at " + endTime.format(FORMATTER) +
                "\n Comment: " + dto.getClientComment();
    }
    public String prepareCancelledMessageToClient(AppointmentCancelledDTO dto){
        ZoneId providerZone = ZoneId.of(dto.getTimezone());
        OffsetDateTime startTime = dto.getStartTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        OffsetDateTime endTime = dto.getEndTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        return "Your appointment was cancelled" +
                "\n Appointment details: " +
                "\n Appointment starts at " + startTime.format(FORMATTER) +
                "\n Appointment ends at " + endTime.format(FORMATTER) +
                "\n Comment: " + dto.getClientComment() +
                "\n Comment for provider: " + dto.getReason();
    }
    public String prepareCancelledMessageToProvider(AppointmentCreateDTO dto){
        ZoneId providerZone = ZoneId.of(dto.getTimezone());
        OffsetDateTime startTime = dto.getStartTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        OffsetDateTime endTime = dto.getEndTime().atZoneSameInstant(providerZone).toOffsetDateTime();
        return "Your appointment was cancelled by client" +
                "\n Appointment details: " +
                "\n Appointment starts at " + startTime.format(FORMATTER) +
                "\n Appointment ends at " + endTime.format(FORMATTER) +
                "\n Comment: " + dto.getClientComment();
    }
}
