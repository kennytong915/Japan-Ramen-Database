package com.ramendirectory.japanramendirectory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ramendirectory.japanramendirectory.security.RsaKeyProperties;


@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JapanramendirectoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(JapanramendirectoryApplication.class, args);
	}

}
