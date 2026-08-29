package com.platform.booking.recording.ProvidersService.KafkaTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCancelledForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.services.KafkaAppointmentProducerService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KafkaAppointmentProducerServiceIntegrationTest.TestConfig.class)
class KafkaAppointmentProducerServiceIntegrationTest extends AbstractBaseKafkaTest {

    @Import({KafkaAppointmentProducerService.class})
    @ImportAutoConfiguration({KafkaAutoConfiguration.class})
    static class TestConfig {

        private <T> ProducerFactory<String, T> createProducerFactory(String typeMapping) {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
            configProps.put(JacksonJsonSerializer.TYPE_MAPPINGS, typeMapping);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        // 1. AppointmentCreate/Confirmed/Deleted Template
        @Bean
        public KafkaTemplate<String, AppointmentForKafkaDTO> appointmentCreateKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "AppointmentForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentForKafkaDTO"
            ));
        }

        // 2. AppointmentCancelled Template
        @Bean
        public KafkaTemplate<String, AppointmentCancelledForKafkaDTO> appointmentCancelledKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "AppointmentCancelledForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCancelledForKafkaDTO"
            ));
        }
    }

    @Autowired
    private KafkaAppointmentProducerService producerService;

    @MockitoBean
    private AppointmentService appointmentService;

    private Consumer<String, AppointmentForKafkaDTO> createConsumer;
    private Consumer<String, AppointmentCancelledForKafkaDTO> cancelledConsumer;

    private static final String CREATE_TOPIC = "appointment-create-topic";
    private static final String CANCELLED_TOPIC = "appointment-cancelled-topic";

    @BeforeEach
    void setUp() {
        // Setup Consumer for AppointmentCreateForKafkaDTO
        Map<String, Object> createConsumerProps = KafkaTestUtils.consumerProps(
                kafka.getBootstrapServers(),
                "test-group-create-" + UUID.randomUUID(),
                true
        );
        createConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, Object> createDeserializerProps = new HashMap<>();
        createDeserializerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        createDeserializerProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS,
                "AppointmentForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCreateForKafkaDTO");

        JacksonJsonDeserializer<AppointmentForKafkaDTO> createDeserializer = new JacksonJsonDeserializer<>(AppointmentForKafkaDTO.class);
        createDeserializer.configure(createDeserializerProps, false);

        DefaultKafkaConsumerFactory<String, AppointmentForKafkaDTO> createConsumerFactory =
                new DefaultKafkaConsumerFactory<>(createConsumerProps, new StringDeserializer(), createDeserializer);

        createConsumer = createConsumerFactory.createConsumer();
        createConsumer.subscribe(Collections.singletonList(CREATE_TOPIC));

        // Setup Consumer for AppointmentCancelledForKafkaDTO
        Map<String, Object> cancelledConsumerProps = KafkaTestUtils.consumerProps(
                kafka.getBootstrapServers(),
                "test-group-cancelled-" + UUID.randomUUID(),
                true
        );
        cancelledConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, Object> cancelledDeserializerProps = new HashMap<>();
        cancelledDeserializerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        cancelledDeserializerProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS,
                "AppointmentCancelledForKafkaDTO:com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCancelledForKafkaDTO");

        JacksonJsonDeserializer<AppointmentCancelledForKafkaDTO> cancelledDeserializer = new JacksonJsonDeserializer<>(AppointmentCancelledForKafkaDTO.class);
        cancelledDeserializer.configure(cancelledDeserializerProps, false);

        DefaultKafkaConsumerFactory<String, AppointmentCancelledForKafkaDTO> cancelledConsumerFactory =
                new DefaultKafkaConsumerFactory<>(cancelledConsumerProps, new StringDeserializer(), cancelledDeserializer);

        cancelledConsumer = cancelledConsumerFactory.createConsumer();
        cancelledConsumer.subscribe(Collections.singletonList(CANCELLED_TOPIC));
    }

    @AfterEach
    void tearDown() {
        if (createConsumer != null) {
            createConsumer.close();
        }
        if (cancelledConsumer != null) {
            cancelledConsumer.close();
        }
        MDC.clear();
    }

    @Test
    @DisplayName("sendToCreate: Publishes AppointmentCreateForKafkaDTO and updates reminder status")
    void sendToCreate_PublishesMessageAndUpdatesService() {
        // Arrange
        String expectedTraceId = "trace-appointment-create-100";
        MDC.put("traceId", expectedTraceId);

        UUID token = UUID.randomUUID();
        AppointmentCreateForKafkaDTO dto = new AppointmentCreateForKafkaDTO();
        dto.setSecureToken(token);
        dto.setClientEmail("client@example.com");
        dto.setClientName("John Doe");
        dto.setStartTime(OffsetDateTime.now());
        dto.setEndTime(OffsetDateTime.now().plusHours(1));

        // Act
        producerService.sendToCreate(dto);

        // Assert: Check Kafka Message
        ConsumerRecords<String, AppointmentForKafkaDTO> records = KafkaTestUtils.getRecords(createConsumer, Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        ConsumerRecord<String, AppointmentForKafkaDTO> record = records.records(CREATE_TOPIC).iterator().next();
        assertThat(record.value()).isNotNull();
        assertThat(record.value().getSecureToken()).isEqualTo(token);
        assertThat(record.value().getClientEmail()).isEqualTo("client@example.com");

        byte[] traceIdHeaderBytes = record.headers().lastHeader("traceId").value();
        assertThat(traceIdHeaderBytes).isNotNull();
        assertThat(new String(traceIdHeaderBytes)).isEqualTo(expectedTraceId);

        // Assert: Check Service Invocation
        verify(appointmentService).setIsRemindedSentToTrue(token);
    }

    @Test
    @DisplayName("sendToCancelled: Publishes AppointmentCancelledForKafkaDTO and updates reminder status")
    void sendToCancelled_PublishesMessageAndUpdatesService() {
        // Arrange
        String expectedTraceId = "trace-appointment-cancelled-200";
        MDC.put("traceId", expectedTraceId);

        UUID token = UUID.randomUUID();
        AppointmentCancelledForKafkaDTO dto = new AppointmentCancelledForKafkaDTO();
        dto.setSecureToken(token);
        dto.setClientEmail("client@example.com");
        dto.setReason("Client request");

        // Act
        producerService.sendToCancelled(dto);

        // Assert: Check Kafka Message
        ConsumerRecords<String, AppointmentCancelledForKafkaDTO> records = KafkaTestUtils.getRecords(cancelledConsumer, Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        ConsumerRecord<String, AppointmentCancelledForKafkaDTO> record = records.records(CANCELLED_TOPIC).iterator().next();
        assertThat(record.value()).isNotNull();
        assertThat(record.value().getSecureToken()).isEqualTo(token);
        assertThat(record.value().getReason()).isEqualTo("Client request");

        // Assert: Check Service Invocation
        verify(appointmentService).setIsRemindedSentToTrue(token);
    }
}
