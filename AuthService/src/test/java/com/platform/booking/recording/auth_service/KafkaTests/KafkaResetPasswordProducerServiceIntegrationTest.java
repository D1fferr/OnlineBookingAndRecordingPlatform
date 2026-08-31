package com.platform.booking.recording.auth_service.KafkaTests;

import com.platform.booking.recording.auth_service.models.ResetPassword;
import com.platform.booking.recording.auth_service.services.KafkaResetPasswordProducerService;
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

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KafkaResetPasswordProducerServiceIntegrationTest.TestConfig.class)
class KafkaResetPasswordProducerServiceIntegrationTest extends AbstractBaseKafkaTest {

    @Import({KafkaResetPasswordProducerService.class})
    @ImportAutoConfiguration({KafkaAutoConfiguration.class})
    static class TestConfig {

        @Bean
        public ProducerFactory<String, ResetPassword> userResetPasswordProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
            configProps.put(JacksonJsonSerializer.TYPE_MAPPINGS,
                    "ResetPassword:com.platform.booking.recording.auth_service.models.ResetPassword");
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, ResetPassword> userResetPasswordKafkaTemplate() {
            return new KafkaTemplate<>(userResetPasswordProducerFactory());
        }
    }

    @Autowired
    private KafkaResetPasswordProducerService producerService;

    private Consumer<String, ResetPassword> testConsumer;
    private static final String TOPIC = "reset-password-topic";

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafka.getBootstrapServers(),
                "test-group-" + UUID.randomUUID(),
                true
        );
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, Object> deserializerProps = new HashMap<>();
        deserializerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        deserializerProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS,
                "ResetPassword:com.platform.booking.recording.auth_service.models.ResetPassword");

        JacksonJsonDeserializer<ResetPassword> jsonDeserializer = new JacksonJsonDeserializer<>(ResetPassword.class);
        jsonDeserializer.configure(deserializerProps, false);

        DefaultKafkaConsumerFactory<String, ResetPassword> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), jsonDeserializer);

        testConsumer = consumerFactory.createConsumer();
        testConsumer.subscribe(Collections.singletonList(TOPIC));
    }

    @AfterEach
    void tearDown() {
        if (testConsumer != null) {
            testConsumer.close();
        }
        MDC.clear();
    }

    @Test
    @DisplayName("send: Publishes ResetPassword entity to 'reset-password-topic' with traceId header")
    void send_PublishesResetPasswordToKafka_Successfully() {
        // Arrange
        String expectedTraceId = "trace-reset-pass-998877";
        MDC.put("traceId", expectedTraceId);

        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setEmail("user.reset@example.com");
        resetPassword.setCode("123456");
        resetPassword.setTtlInSeconds(900L);

        // Act
        producerService.send(resetPassword);

        // Assert
        ConsumerRecords<String, ResetPassword> records = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        ConsumerRecord<String, ResetPassword> record = records.records(TOPIC).iterator().next();
        assertThat(record.value()).isNotNull();
        assertThat(record.value().getEmail()).isEqualTo("user.reset@example.com");
        assertThat(record.value().getCode()).isEqualTo("123456");
        assertThat(record.value().getTtlInSeconds()).isEqualTo(900L);

        byte[] traceIdHeaderBytes = record.headers().lastHeader("traceId").value();
        assertThat(traceIdHeaderBytes).isNotNull();
        assertThat(new String(traceIdHeaderBytes)).isEqualTo(expectedTraceId);
    }
}
