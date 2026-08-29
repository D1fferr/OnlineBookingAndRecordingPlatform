package com.platform.booking.recording.EmailService.KafkaTests;

import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.dtos.ResetPasswordDTO;
import com.platform.booking.recording.EmailService.services.EmailUserDataSenderService;
import com.platform.booking.recording.EmailService.services.KafkaChangeUserDataConsumerService;
import com.platform.booking.recording.EmailService.util.KafkaTraceIdInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KafkaChangeUserDataConsumerServiceIntegrationTest.TestConfig.class)
class KafkaChangeUserDataConsumerServiceIntegrationTest extends AbstractBaseKafkaTest {

    @EnableKafka
    @Import({KafkaChangeUserDataConsumerService.class, KafkaTraceIdInterceptor.class})
    @ImportAutoConfiguration({KafkaAutoConfiguration.class})
    static class TestConfig {

        private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> clazz) {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service-test-" + UUID.randomUUID());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            JacksonJsonDeserializer<T> deserializer = new JacksonJsonDeserializer<>(clazz);
            deserializer.addTrustedPackages("*");
            deserializer.setUseTypeHeaders(false);

            return new DefaultKafkaConsumerFactory<>(
                    props,
                    new StringDeserializer(),
                    new ErrorHandlingDeserializer<>(deserializer)
            );
        }

        @Bean
        public DefaultErrorHandler errorHandler() {
            return new DefaultErrorHandler(new FixedBackOff(1000L, 1));
        }

        @Bean
        public ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> traceIdContainerCustomizer(
                RecordInterceptor<Object, Object> kafkaTraceIdInterceptor,
                DefaultErrorHandler errorHandler) {
            return container -> {
                container.setRecordInterceptor(kafkaTraceIdInterceptor);
                container.setCommonErrorHandler(errorHandler);
            };
        }

        @SuppressWarnings("unchecked")
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> providerRegistrationKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(ProviderCreateDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @SuppressWarnings("unchecked")
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ResetPasswordDTO> providerResetPasswordKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, ResetPasswordDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(ResetPasswordDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @Bean
        public KafkaTemplate<String, Object> testKafkaTemplate() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

            ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(props);
            return new KafkaTemplate<>(producerFactory);
        }
    }

    @Autowired
    private KafkaTemplate<String, Object> testKafkaTemplate;

    @MockitoBean
    private EmailUserDataSenderService emailUserDataSenderService;

    @Test
    @DisplayName("getProvider: Consumes message from 'user-topic' and sends provider registration email")
    void getProvider_ConsumesMessageSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(id);
        dto.setName("Dr. John Watson");
        dto.setEmail("watson@example.com");
        dto.setServiceType("Medical");
        dto.setTimezone("UTC");

        // Act
        testKafkaTemplate.send("user-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(emailUserDataSenderService).sendRegistrationProvider(argThat(actual ->
                        id.equals(actual.getId()) &&
                                "watson@example.com".equals(actual.getEmail())
                )));
    }

    @Test
    @DisplayName("getResetPassword: Consumes message from 'reset-password-topic' and sends password reset code email")
    void getResetPassword_ConsumesMessageSuccessfully() {
        // Arrange
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail("user.reset@example.com");
        dto.setCode("654321");
        dto.setTtlInSeconds(600L);

        // Act
        testKafkaTemplate.send("reset-password-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(emailUserDataSenderService).sendResetPasswordCode(argThat(actual ->
                        "user.reset@example.com".equals(actual.getEmail()) &&
                                "654321".equals(actual.getCode()) &&
                                Long.valueOf(600L).equals(actual.getTtlInSeconds())
                )));
    }
}
