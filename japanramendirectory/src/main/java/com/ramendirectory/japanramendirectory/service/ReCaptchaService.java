package com.ramendirectory.japanramendirectory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ReCaptchaService {

    @Value("${google.recaptcha.key.secret}")
    private String secretKey;
    
    @Value("${google.recaptcha.url}")
    private String verifyUrl;
    
    private final WebClient webClient;
    
    public ReCaptchaService() {
        this.webClient = WebClient.builder().build();
    }
    
    public boolean validateCaptcha(String captchaResponse) {
        if (!StringUtils.hasText(captchaResponse)) {
            return false;
        }
        
        try {
            Map<String, Object> response = webClient.post()
                .uri(verifyUrl + "?secret={secret}&response={response}", secretKey, captchaResponse)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
                
            if (response == null) {
                return false;
            }
            
            return Boolean.TRUE.equals(response.get("success"));
            
        } catch (Exception e) {
            return false;
        }
    }
} 