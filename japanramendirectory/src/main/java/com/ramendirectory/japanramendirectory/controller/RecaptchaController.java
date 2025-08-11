package com.ramendirectory.japanramendirectory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recaptcha")
public class RecaptchaController {

    @Value("${google.recaptcha.key.site}")
    private String recaptchaSiteKey;

    @GetMapping("/sitekey")
    public Map<String, String> getSiteKey() {
        Map<String, String> response = new HashMap<>();
        response.put("siteKey", recaptchaSiteKey);
        return response;
    }
} 