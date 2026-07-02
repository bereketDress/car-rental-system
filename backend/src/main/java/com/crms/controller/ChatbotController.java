package com.crms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message").toLowerCase();

        String reply;

        if (message.contains("rent") || message.contains("reservation")) {
            reply = "To rent a car, go to the Cars page, choose a car, and create a reservation.";
        } else if (message.contains("payment")) {
            reply = "You can make payments from the Payments page after creating a reservation.";
        } else if (message.contains("branch")) {
            reply = "You can view available branches from the Branches page.";
        } else if (message.contains("login")) {
            reply = "Click Login and enter your email and password.";
        } else {
            reply = "Sorry, I can help with reservations, payments, branches, and login questions.";
        }

        return Map.of("reply", reply);
    }
}
