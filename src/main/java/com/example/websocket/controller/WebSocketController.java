package com.example.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello") // Получает сообщения от клиента
    @SendTo("/topic/greetings") // Отправляет сообщения всем подписчикам
    public String handleMessage(String message) {
        return "Привет, " + message + " от сервера!";
    }
}

