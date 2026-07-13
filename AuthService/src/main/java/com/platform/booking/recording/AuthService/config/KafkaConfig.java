package com.platform.booking.recording.AuthService.config;

import com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthService.models.ResetPassword;
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
    private final String kafkaEndpoint = config.getKafka().getEndpoint();

    @Bean
    public ProducerFactory<String, UserForKafkaDTO> userRegistrationProducerFactory(){
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "UserForKafkaDTO:com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, UserForKafkaDTO> userRegistrationKafkaTemplate(){
        return new KafkaTemplate<>(userRegistrationProducerFactory());
    }
    @Bean
    public ProducerFactory<String, ResetPassword> userResetPasswordProducerFactory(){
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "UserForKafkaDTO:com.platform.booking.recording.AuthService.models.ResetPassword");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, UserForKafkaDTO> userResetPasswordKafkaTemplate(){
        return new KafkaTemplate<>(userRegistrationProducerFactory());
    }


}
