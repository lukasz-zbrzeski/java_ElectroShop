package com.example.shopapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThankYouController {
    @GetMapping("/thank-you")
    public String thankYouPage() {
        return "thank-you";
    }
}
