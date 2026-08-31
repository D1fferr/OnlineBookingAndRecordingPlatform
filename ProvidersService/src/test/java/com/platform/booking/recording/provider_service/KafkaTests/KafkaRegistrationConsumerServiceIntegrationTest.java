package com.platform.booking.recording.provider_service.KafkaTests;


import com.platform.booking.recording.provider_service.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.provider_service.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.provider_service.services.KafkaRegistrationConsumerService;
import com.platform.booking.recording.provider_service.services.ProviderService;
import com.platform.booking.recording.provider_service.util.KafkaTraceIdInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
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
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KafkaRegistrationConsumerServiceIntegrationTest.TestConfig.class)
class KafkaRegistrationConsumerServiceIntegrationTest extends AbstractBaseKafkaTest {

    @EnableKafka
    @Import({KafkaRegistrationConsumerService.class, KafkaTraceIdInterceptor.class})
    @ImportAutoConfiguration({KafkaAutoConfiguration.class})
    static class TestConfig {

        private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> clazz) {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "provider-service-test-" + UUID.randomUUID());
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

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> providerRegistrationKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, ProviderCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(ProviderCreateDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ProviderUpdateEmailDTO> providerEmailKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, ProviderUpdateEmailDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(ProviderUpdateEmailDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ProviderIsBlockedDTO> providerIsBlockedKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, ProviderIsBlockedDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(ProviderIsBlockedDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, UserAvatarForKafkaDTO> providerAvatarKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, UserAvatarForKafkaDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(UserAvatarForKafkaDTO.class));
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
    private ProviderService providerService;

    @Test
    @DisplayName("getProvider(ProviderCreateDTO): Consumes message, executes interceptor setting MDC traceId")
    void getProvider_CreateDTO_ConsumesMessageAndSetsMdcTraceId() {
        // Arrange
        String expectedTraceId = "custom-trace-id-777";
        AtomicReference<String> mdcTraceIdDuringExecution = new AtomicReference<>();

        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("Dr. House");
        dto.setEmail("house@example.com");

        // Capture MDC traceId during providerService.save execution
        doAnswer(invocation -> {
            mdcTraceIdDuringExecution.set(MDC.get("traceId"));
            return null;
        }).when(providerService).save(any(ProviderCreateDTO.class));

        Message<ProviderCreateDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "user-topic")
                .setHeader("traceId", expectedTraceId.getBytes(StandardCharsets.UTF_8))
                .build();

        // Act
        testKafkaTemplate.send(message);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(providerService).save(refEq(dto));
                    assertThat(mdcTraceIdDuringExecution.get()).isEqualTo(expectedTraceId);
                });
    }

    @Test
    @DisplayName("getProvider(ProviderUpdateEmailDTO): Consumes message from 'user-email-topic'")
    void getProvider_UpdateEmailDTO_ConsumesMessageSuccessfully() {
        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("new.email@example.com");

        testKafkaTemplate.send("user-email-topic", dto);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(providerService).updateEmail(refEq(dto)));
    }

    @Test
    @DisplayName("getProvider(ProviderIsBlockedDTO): Consumes message from 'user-is-blocked-topic'")
    void getProvider_IsBlockedDTO_ConsumesMessageSuccessfully() {
        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO();
        dto.setId(UUID.randomUUID());
        dto.setIsBlocked(true);

        testKafkaTemplate.send("user-is-blocked-topic", dto);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(providerService).updateIsBlocked(refEq(dto)));
    }

    @Test
    @DisplayName("getProvider(UserAvatarForKafkaDTO): Consumes message from 'user-avatar-topic'")
    void getProvider_UserAvatarDTO_ConsumesMessageSuccessfully() {
        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO();
        dto.setId(UUID.randomUUID());
        dto.setAvatarURL("https://example.com/new-avatar.jpg");

        testKafkaTemplate.send("user-avatar-topic", dto);

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(providerService).updateAvatar(refEq(dto)));
    }
}
