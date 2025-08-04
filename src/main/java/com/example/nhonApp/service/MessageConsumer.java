package com.example.nhonApp.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @KafkaListener(topics = "new-order", groupId = "order_group")
    public void listenNewOrder(String message) {
        System.out.println("Received new order message: " + message);
    }

    @KafkaListener(topics = "payment", groupId = "payment_group")
    public void listenPayment(String message) {
        System.out.println("Received payment message: " + message);
    }
}
