package com.ramendirectory.japanramendirectory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ReCaptchaConfig implements WebMvcConfigurer {

    @Value("${google.recaptcha.key.site}")
    private String siteKey;

    public String getSiteKey() {
        return siteKey;
    }
} 