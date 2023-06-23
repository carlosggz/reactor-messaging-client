package org.example.reactormessagingclient.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyService {

    public void doSomething(String value, String routingKey) {
        log.info("Received value: {}, with routing key: {}", value, routingKey);
    }
}
