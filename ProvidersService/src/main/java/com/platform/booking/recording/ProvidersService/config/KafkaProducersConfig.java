package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateForKafkaDTO;
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
    private final String kafkaEndpoint = config.getKafka().getEndpoint();
    @Bean
    public ProducerFactory<String, AppointmentCreateForKafkaDTO> appointmentCreateProducerFactory(){
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "AppointmentCreateForKafkaDTO:package com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, AppointmentCreateForKafkaDTO> appointmentCreateKafkaTemplate(){
        return new KafkaTemplate<>(appointmentCreateProducerFactory());
    }
}
