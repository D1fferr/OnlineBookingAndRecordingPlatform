package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
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
@EnableKafka
public class KafkaConfig {
    private final ExternalConfig config;

    @Bean
    public ConsumerFactory<String, ProviderCreateDTO> providerRegistrationConsumerFactory() {
        String kafkaEndpoint = config.getKafka().getEndpoint();
        Map<String, Object> configKafkaConsumer = new HashMap<>();
        configKafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
        configKafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service");
        configKafkaConsumer.put(JacksonJsonSerializer.TYPE_MAPPINGS, "ProviderCreateDTO:com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO");
        JacksonJsonDeserializer<ProviderCreateDTO> jacksonDeserializer =
                new JacksonJsonDeserializer<>(ProviderCreateDTO.class);
        return new DefaultKafkaConsumerFactory<>(
                configKafkaConsumer,
                new StringDeserializer(),
                jacksonDeserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> providerRegistrationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(providerRegistrationConsumerFactory());
        return factory;
    }



}
