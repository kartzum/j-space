package io.rdlab.amq.art;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("resource")
public class MessagingTests {
    private static final int AMQ_PORT = 61616;

    private static final String MQTT_SERVER_HOST = "0.0.0.0";
    private static final int MQTT_SERVER_PORT = 1883;

    private static final String USERNAME = "u";
    private static final String PASSWORD = "p";

    private static final DockerComposeContainer<?> environment;

    static {
        environment = new DockerComposeContainer<>(
                new File("src/test/resources/docker-compose.yaml"))
                .withExposedService("artemis", AMQ_PORT, Wait.forListeningPort());
    }

    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeAll
    static void beforeAll() {
        environment.start();
    }

    @AfterAll
    static void afterAll() {
        environment.stop();
    }

    @Test
    void simpleJmsTest() throws JMSException {
        String queue = "testQueue";
        jmsTemplate.convertAndSend(queue, "Hello, JMS!");
        Message message = jmsTemplate.receive(queue);
        assertThat(message).isInstanceOf(TextMessage.class);
        TextMessage textMessage = (TextMessage) message;
        assertNotNull(textMessage);
        assertThat(textMessage.getText()).isEqualTo("Hello, JMS!");
    }

    @Test
    void simpleMqttTest() {
        AtomicReference<String> messageReference = new AtomicReference<>();

        String topic = "the/topic";

        Mqtt5AsyncClient clientA = MqttClient
                .builder()
                .useMqttVersion5()
                .identifier(UUID.randomUUID().toString())
                .serverHost(MQTT_SERVER_HOST)
                .serverPort(MQTT_SERVER_PORT)
                .buildAsync();

        clientA.connectWith().
                simpleAuth()
                .username(USERNAME)
                .password(PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .join();

        clientA.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    String message = new String(publish.getPayloadAsBytes(), Charset.defaultCharset());
                    messageReference.set(message);
                })
                .send()
                .join();

        Mqtt5AsyncClient clientB = MqttClient
                .builder()
                .useMqttVersion5()
                .identifier(UUID.randomUUID().toString())
                .serverHost(MQTT_SERVER_HOST)
                .serverPort(MQTT_SERVER_PORT)
                .buildAsync();

        clientB.connectWith().
                simpleAuth()
                .username(USERNAME)
                .password(PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .join();

        clientB.publishWith()
                .topic(topic)
                .payload("hello world".getBytes())
                .send()
                .join();

        Awaitility.await()
                .timeout(5, TimeUnit.SECONDS)
                .pollDelay(4, TimeUnit.SECONDS)
                .until(() -> true);

        assertEquals("hello world", messageReference.get());
    }
}
