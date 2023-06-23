package org.example.reactormessagingclient;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Awaitility;
import org.example.reactormessaging.domain.components.ReactorEventPublisher;
import org.example.reactormessaging.domain.models.KafkaMessage;
import org.example.reactormessaging.domain.models.RabbitMessage;
import org.example.reactormessagingclient.models.CustomDto;
import org.example.reactormessagingclient.services.MyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@EmbeddedKafka(
        topics = "customEvents",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        partitions = 2)
@Slf4j
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
class ReactorMessagingClientApplicationTests {

    @Autowired
    ReactorEventPublisher eventPublisher;

    @Autowired
    @SpyBean
    MyService myService;

    @Test
    void contextLoads() {
    }

    @Test
    void messageRoutedOkIsReceived() {
        //given
        val message = "test ok";
        val key = "123.ok";

        //when
        eventPublisher
                .sendMessage(RabbitMessage.builder()
                        .routingKey(key)
                        .exchangeName("executionRouted")
                        .payload(message)
                        .build())
                .subscribe();

        //then
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> verify(myService).doSomething(eq(message), eq(key)));
    }

    @Test
    void messageRoutedKoIsNotReceived() {
        //given
        val message = "test ko";

        //when
        eventPublisher
                .sendMessage(RabbitMessage.builder()
                        .routingKey("123.ko")
                        .exchangeName("executionRouted")
                        .payload(message)
                        .build())
                .subscribe();

        //then
        Awaitility
                .await()
                .atLeast(Duration.ofSeconds(2));

        verifyNoInteractions(myService);
    }

    @Test
    void messageAccountedOkIsReceived() {
        //given
        val message = CustomDto.builder()
                .id(123)
                .name("some name")
                .build();
        val key = "123.ok";

        //when
        eventPublisher
                .sendMessage(RabbitMessage.builder()
                        .routingKey(key)
                        .exchangeName("executionAccounted")
                        .payload(message)
                        .build())
                .subscribe();

        //then
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> verify(myService).doSomething(eq(message.toString()), eq(key)));
    }

    @Test
    void messageEventsIsReceived() {
        //given
        val message = "test ok";
        val key = "123.ok";

        //when
        eventPublisher
                .sendMessage(KafkaMessage.builder()
                        .routingKey(key)
                        .topic("eventsTopic")
                        .payload(message)
                        .build())
                .subscribe();

        //then
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> verify(myService).doSomething(eq(message), eq(key)));
    }

    @Test
    void messageCustomIsReceived() {
        //given
        val message = CustomDto.builder()
                .id(123)
                .name("my-name")
                .build();
        val key = "123.ok";

        //when
        eventPublisher
                .sendMessage(KafkaMessage.builder()
                        .routingKey(key)
                        .topic("customTopic")
                        .payload(message)
                        .build())
                .subscribe();

        //then
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> verify(myService).doSomething(eq(message.toString()), eq(key)));
    }
}
