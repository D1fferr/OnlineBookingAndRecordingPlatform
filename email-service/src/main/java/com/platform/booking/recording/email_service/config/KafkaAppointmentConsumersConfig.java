package com.platform.booking.recording.email_service.config;

import com.platform.booking.recording.email_service.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.email_service.dtos.AppointmentCreateDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAppointmentConsumersConfig {
    private final ExternalConfig config;


    @Bean
    public ConsumerFactory<String, AppointmentCreateDTO> providerAppointmentConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
        JacksonJsonDeserializer<AppointmentCreateDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(AppointmentCreateDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCreateDTO> providerAppointmentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerAppointmentConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, AppointmentCancelledDTO> providerAppointmentCancelledConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
        JacksonJsonDeserializer<AppointmentCancelledDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(AppointmentCancelledDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> providerAppointmentCancelledKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerAppointmentCancelledConsumerFactory());
        return factory;
    }



}
