package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableKafka
public class KafkaConfig {
    private final ExternalConfig config;
    private final String kafkaEndpoint = config.getKafka().getEndpoint();

    @Bean
    public ConsumerFactory<String, ProviderCreateDTO> providerRegistrationConsumerFactory() {
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
