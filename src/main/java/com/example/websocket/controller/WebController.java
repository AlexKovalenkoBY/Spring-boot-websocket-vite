package com.example.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller
// public class WebController {
//     @GetMapping("/{path:[^\\.]*}") // Переадресовать запросы, кроме запросов к файлам
//     public String forward() {
//         return "forward:/index.html";
//     }
// }
@Controller
public class WebController {
    // Исключите путь /ws из переадресации
    @GetMapping(value = {"/", "/index", "/home"})
    public String forward() {
        return "forward:/index.html";
    }
}