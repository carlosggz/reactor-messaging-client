package org.example.reactormessagingclient.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reactormessagingclient.models.CustomDto;
import org.example.reactormessagingclient.services.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@EnableWebFlux
@Slf4j
public class AppConfiguration {
    private final MyService myService;

    @Bean
    Consumer<Message<String>> fromRouted() {
        return message -> myService.doSomething(message.getPayload());
    }

    @Bean
    Consumer<Message<CustomDto>> fromAccounting() {
        return message -> myService.doSomething(message.getPayload().toString());
    }

    @Bean
    Consumer<Message<String>> fromEvents()  {
        return message -> myService.doSomething(message.getPayload());
    }

    @Bean
    public Consumer<Message<CustomDto>> fromCustom()   {
        return message -> myService.doSomething(message.getPayload().toString());
    }

    @Bean
    public Consumer<Message<String>> fromError() {
        return message -> {
            log.info("Raising error...");
            throw new RuntimeException("Custom error");
        };
    }
}
