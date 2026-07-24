package com.platform.booking.recording.EmailService.config;

import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAppointmentConsumersConfig {
    private final ExternalConfig config;
    private final String kafkaEndpoint = config.getKafka().getEndpoint();


    @Bean
    public ConsumerFactory<String, AppointmentCreateDTO> providerAppointmentConsumerFactory() {
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<AppointmentCreateDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(AppointmentCreateDTO.class);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                jacksonDeserializer
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
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<AppointmentCancelledDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(AppointmentCancelledDTO.class);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                jacksonDeserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> providerAppointmentCancelledKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerAppointmentCancelledConsumerFactory());
        return factory;
    }



}
