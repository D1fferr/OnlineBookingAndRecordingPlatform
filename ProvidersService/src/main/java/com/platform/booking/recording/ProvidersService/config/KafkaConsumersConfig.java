package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderUpdateEmailDTO;
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
    private final String kafkaEndpoint = config.getKafka().getEndpoint();

    @Bean
    public ConsumerFactory<String, ProviderCreateDTO> providerRegistrationConsumerFactory() {
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

}
