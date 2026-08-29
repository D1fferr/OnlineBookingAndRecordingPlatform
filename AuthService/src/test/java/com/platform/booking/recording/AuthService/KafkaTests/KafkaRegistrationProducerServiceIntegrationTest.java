package com.platform.booking.recording.AuthService.KafkaTests;

import com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.AuthService.dtos.UserAvatarForKafkaDTO;
import com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.AuthService.services.ImageService;
import com.platform.booking.recording.AuthService.services.KafkaRegistrationProducerService;
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
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(classes = KafkaRegistrationProducerServiceIntegrationTest.TestConfig.class)
class KafkaRegistrationProducerServiceIntegrationTest extends AbstractBaseKafkaTest {
    @Import({KafkaRegistrationProducerService.class})
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

        // 1. UserRegistration Template
        @Bean
        public KafkaTemplate<String, UserForKafkaDTO> userRegistrationKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "UserForKafkaDTO:com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO"
            ));
        }

        // 2. UserEmail Template
        @Bean
        public KafkaTemplate<String, ProviderUpdateEmailDTO> userEmailKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "ProviderUpdateEmailDTO:com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO"
            ));
        }

        // 3. UserIsBlocked Template
        @Bean
        public KafkaTemplate<String, ProviderIsBlockedDTO> userIsBlockedKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "ProviderIsBlockedDTO:com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO"
            ));
        }

        // 4. UserAvatar Template
        @Bean
        public KafkaTemplate<String, UserAvatarForKafkaDTO> userAvatarKafkaTemplate() {
            return new KafkaTemplate<>(createProducerFactory(
                    "UserAvatarForKafkaDTO:com.platform.booking.recording.AuthService.dtos.UserAvatarForKafkaDTO"
            ));
        }
    }
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private KafkaRegistrationProducerService producerService;

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private TransactionTemplate transactionTemplate;

    private Consumer<String, UserForKafkaDTO> testConsumer;
    private static final String TOPIC = "user-topic";

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            java.util.function.Consumer<?> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        // Configure test Kafka Consumer for reading from Testcontainers Kafka
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafka.getBootstrapServers(),
                "test-group-" + UUID.randomUUID(),
                true
        );
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, Object> deserializerProps = new HashMap<>();
        deserializerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        deserializerProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS,
                "UserForKafkaDTO:com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO");

        JacksonJsonDeserializer<UserForKafkaDTO> jsonDeserializer = new JacksonJsonDeserializer<>(UserForKafkaDTO.class);
        jsonDeserializer.configure(deserializerProps, false);

        DefaultKafkaConsumerFactory<String, UserForKafkaDTO> consumerFactory =
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
    @DisplayName("send: Publishes UserForKafkaDTO to 'user-topic' with traceId header")
    void send_PublishesMessageToKafka_Successfully() {
        // Arrange
        String expectedTraceId = "test-trace-id-12345";
        MDC.put("traceId", expectedTraceId);

        UserForKafkaDTO dto = new UserForKafkaDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("kafka.test@example.com");
        dto.setTimezone(TimeZone.getDefault().toString());
        dto.setName("Name");
        dto.setServiceType("Service type");

        // Act: Trigger send via producerService directly or via event execution
        transactionTemplate.executeWithoutResult(status -> producerService.send(dto));

        // Assert: Read record from Kafka container
        ConsumerRecords<String, UserForKafkaDTO> records = KafkaTestUtils.getRecords(testConsumer, Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        ConsumerRecord<String, UserForKafkaDTO> record = records.records(TOPIC).iterator().next();
        assertThat(record.value()).isNotNull();
        assertThat(record.value().getEmail()).isEqualTo("kafka.test@example.com");

        byte[] traceIdHeaderBytes = record.headers().lastHeader("traceId").value();
        assertThat(traceIdHeaderBytes).isNotNull();
        assertThat(new String(traceIdHeaderBytes)).isEqualTo(expectedTraceId);
    }
}