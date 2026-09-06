package com.platform.booking.recording.provider_service.config;

import com.platform.booking.recording.provider_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.provider_service.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableKafka
public class KafkaConsumersConfig {
    private final ExternalConfig config;

    @Bean
    public ConsumerFactory<String, ProviderCreateDTO> providerRegistrationConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<ProviderCreateDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(ProviderCreateDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> providerRegistrationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerRegistrationConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ProviderUpdateEmailDTO> providerEmailConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<ProviderUpdateEmailDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(ProviderUpdateEmailDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProviderUpdateEmailDTO> providerEmailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProviderUpdateEmailDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerEmailConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, ProviderIsBlockedDTO> providerIsBlockedConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<ProviderIsBlockedDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(ProviderIsBlockedDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProviderIsBlockedDTO> providerIsBlockedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProviderIsBlockedDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerIsBlockedConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, UserAvatarForKafkaDTO> providerAvatarConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service");
        configKafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        JacksonJsonDeserializer<UserAvatarForKafkaDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(UserAvatarForKafkaDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserAvatarForKafkaDTO> providerAvatarKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserAvatarForKafkaDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerAvatarConsumerFactory());
        return factory;
    }

}
