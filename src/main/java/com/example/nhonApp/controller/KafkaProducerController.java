package com.example.nhonApp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/messages")
    public String sendMessage(@RequestParam("topic") String topic,
                              @RequestParam("message") String message) {
        kafkaTemplate.send(topic, message);
        return "Message sent to topic " + topic + ": " + message;
    }
}
