package com.platform.booking.recording.AuthService.config;

import com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.AuthService.dtos.UserAvatarForKafkaDTO;
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

    @Bean
    public ProducerFactory<String, UserForKafkaDTO> userRegistrationProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
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
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "ResetPassword:com.platform.booking.recording.AuthService.models.ResetPassword");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, ResetPassword> userResetPasswordKafkaTemplate(){
        return new KafkaTemplate<>(userResetPasswordProducerFactory());
    }
    @Bean
    public ProducerFactory<String, ProviderUpdateEmailDTO> userEmailProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "ProviderUpdateEmailDTO:com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, ProviderUpdateEmailDTO> userEmailKafkaTemplate(){
        return new KafkaTemplate<>(userEmailProducerFactory());
    }
    @Bean
    public ProducerFactory<String, UserAvatarForKafkaDTO> userAvatarProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "UserAvatarForKafkaDTO:com.platform.booking.recording.AuthService.dtos.UserAvatarForKafkaDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, UserAvatarForKafkaDTO> userAvatarKafkaTemplate(){
        return new KafkaTemplate<>(userAvatarProducerFactory());
    }

    @Bean
    public ProducerFactory<String, ProviderIsBlockedDTO> userIsBlockedProducerFactory(){
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaProducer = new HashMap<>();
        configKafkaProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configKafkaProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configKafkaProducer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "ProviderIsBlockedDTO:com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO");
        return new DefaultKafkaProducerFactory<>(configKafkaProducer);
    }
    @Bean
    public KafkaTemplate<String, ProviderIsBlockedDTO> userIsBlockedKafkaTemplate() {
        return new KafkaTemplate<>(userIsBlockedProducerFactory());
    }


}
