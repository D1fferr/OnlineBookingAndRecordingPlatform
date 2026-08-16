package com.platform.booking.recording.EmailService.config;

import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
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
public class KafkaProviderConsumersConfig {
    private final ExternalConfig config;

    @Bean
    public ConsumerFactory<String, ProviderCreateDTO> providerRegistrationConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
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
    public ConsumerFactory<String, ResetPasswordDTO> providerResetPasswordConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");
        JacksonJsonDeserializer<ResetPasswordDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(ResetPasswordDTO.class);
        jacksonDeserializer.addTrustedPackages("*");
        jacksonDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jacksonDeserializer)
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResetPasswordDTO> providerResetPasswordKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResetPasswordDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerResetPasswordConsumerFactory());
        return factory;
    }


}
