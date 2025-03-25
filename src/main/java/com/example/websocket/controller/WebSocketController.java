package com.example.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String handleMessage(MessageDTO message, 
                              SimpMessageHeaderAccessor headerAccessor) {
        
        // Получаем IP-адрес клиента
        String ipAddress = headerAccessor.getSessionAttributes()
            .get("IP_ADDRESS").toString();
        
        return "Привет, " + message.getMessage() + " от " + ipAddress + "!";
    }
}

