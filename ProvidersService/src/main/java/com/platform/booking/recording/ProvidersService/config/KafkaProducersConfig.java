package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentForKafkaDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableKafka
public class KafkaProducersConfig {
    private final ExternalConfig config;
    @Bean
    public ProducerFactory<String, AppointmentForKafkaDTO> appointmentCreateProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "AppointmentForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.AppointmentForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, AppointmentForKafkaDTO> appointmentCreateKafkaTemplate(){
        return new KafkaTemplate<>(appointmentCreateProducerFactory());
    }
    @Bean
    public ProducerFactory<String, AppointmentCancelledForKafkaDTO> appointmentCancelledProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "AppointmentCancelledForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, AppointmentCancelledForKafkaDTO> appointmentCancelledKafkaTemplate(){
        return new KafkaTemplate<>(appointmentCancelledProducerFactory());
    }
}
