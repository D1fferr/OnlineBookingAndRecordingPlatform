package com.platform.booking.recording.EmailService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailSenderConfig {

    private final ExternalConfig config;

    @Bean
    public JavaMailSender javaMailSender(){
        String login = config.getMail().getLogin();
        String password = config.getMail().getPassword();
        String host = config.getMail().getHost();
        Integer port = Integer.getInteger(config.getMail().getPort());
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(login);
        mailSender.setPassword(password);
        Properties prop = mailSender.getJavaMailProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.debug", "true");
        prop.put("mail.test-connection", "true");
        mailSender.setJavaMailProperties(prop);
        return mailSender;
    }
}
