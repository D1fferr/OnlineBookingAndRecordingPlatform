package com.platform.booking.recording.EmailService.KafkaTests;

import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.EmailService.services.EmailAppointmentSenderService;
import com.platform.booking.recording.EmailService.services.KafkaAppointmentConsumerService;
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KafkaAppointmentConsumerServiceIntegrationTest.TestConfig.class)
class KafkaAppointmentConsumerServiceIntegrationTest extends AbstractBaseKafkaTest {

    @EnableKafka
    @Import({KafkaAppointmentConsumerService.class, KafkaTraceIdInterceptor.class})
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
        public ConcurrentKafkaListenerContainerFactory<String, AppointmentCreateDTO> providerAppointmentKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, AppointmentCreateDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(AppointmentCreateDTO.class));
            factory.setContainerCustomizer((ContainerCustomizer) customizer);
            return factory;
        }

        @SuppressWarnings("unchecked")
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> providerAppointmentCancelledKafkaListenerContainerFactory(
                ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> customizer) {
            ConcurrentKafkaListenerContainerFactory<String, AppointmentCancelledDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(AppointmentCancelledDTO.class));
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
    private EmailAppointmentSenderService emailAppointmentSenderService;

    @Test
    @DisplayName("getAppointmentCreate: Consumes message from 'appointment-create-topic' and sends emails to client and provider")
    void getAppointmentCreate_ConsumesMessageSuccessfully() {
        // Arrange
        UUID token = UUID.randomUUID();
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(token);
        dto.setClientName("Alice Johnson");
        dto.setClientEmail("alice@example.com");
        dto.setProviderEmail("provider@example.com");
        dto.setStartTime(OffsetDateTime.now());
        dto.setEndTime(OffsetDateTime.now().plusHours(1));

        // Act
        testKafkaTemplate.send("appointment-create-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(emailAppointmentSenderService).sendCreateMessageToClient(argThat(actual ->
                            token.equals(actual.getSecureToken()) &&
                                    "alice@example.com".equals(actual.getClientEmail())
                    ));
                    verify(emailAppointmentSenderService).sendCreateMessageToProvider(argThat(actual ->
                            token.equals(actual.getSecureToken()) &&
                                    "provider@example.com".equals(actual.getProviderEmail())
                    ));
                });
    }

    @Test
    @DisplayName("getAppointmentConfirmed: Consumes message from 'appointment-confirmed-topic' and sends confirmation email")
    void getAppointmentConfirmed_ConsumesMessageSuccessfully() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail("alice@example.com");

        // Act
        testKafkaTemplate.send("appointment-confirmed-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(emailAppointmentSenderService).sendConfirmedMessage(refEq(dto)));
    }

    @Test
    @DisplayName("getAppointmentCancelled: Consumes message from 'appointment-cancelled-topic' and sends cancellation email")
    void getAppointmentCancelled_ConsumesMessageSuccessfully() {
        // Arrange
        AppointmentCancelledDTO dto = new AppointmentCancelledDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail("alice@example.com");
        dto.setReason("Schedule conflict");

        // Act
        testKafkaTemplate.send("appointment-cancelled-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(emailAppointmentSenderService).sendCancelledMessage(refEq(dto)));
    }

    @Test
    @DisplayName("getAppointmentDeleted: Consumes message from 'appointment-deleted-topic' and sends deletion email to provider")
    void getAppointmentDeleted_ConsumesMessageSuccessfully() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setProviderEmail("provider@example.com");

        // Act
        testKafkaTemplate.send("appointment-deleted-topic", dto);

        // Assert
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(emailAppointmentSenderService).sendDeletedMessageToProvider(refEq(dto)));
    }
}