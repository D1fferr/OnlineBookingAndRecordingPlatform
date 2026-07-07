package com.platform.booking.recording.AuthService.config;

import com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final ExternalConfig config;

    @Bean
    public ProducerFactory<String, UserForKafkaDTO> userProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "com.platform.booking.recording.AuthSevice.dtos.UserForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, UserForKafkaDTO> userKafkaTemplate(){
        return new KafkaTemplate<>(userProducerFactory());
    }

}
