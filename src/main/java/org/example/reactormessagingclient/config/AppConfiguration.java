package org.example.reactormessagingclient.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reactormessaging.domain.models.MessageDetails;
import org.example.reactormessagingclient.models.CustomDto;
import org.example.reactormessagingclient.services.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@EnableWebFlux
@Slf4j
public class AppConfiguration {
    private final MyService myService;

    @Bean
    Consumer<MessageDetails<String>> fromRouted() {
        return message -> myService.doSomething(message.getPayload(), message.getRoutingKey());
    }

    @Bean
    Consumer<MessageDetails<CustomDto>> fromAccounting() {
        return message -> myService.doSomething(message.getPayload().toString(), message.getRoutingKey());
    }

    @Bean
    Consumer<MessageDetails<String>> fromEvents()  {
        return message -> myService.doSomething(message.getPayload(), message.getRoutingKey());
    }

    @Bean
    public Consumer<MessageDetails<CustomDto>> fromCustom()   {
        return message -> myService.doSomething(message.getPayload().toString(), message.getRoutingKey());
    }

    @Bean
    public Consumer<MessageDetails<String>> fromError() {
        return message -> {
            log.info("Raising error...");
            throw new RuntimeException("Custom error");
        };
    }
}
