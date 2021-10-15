package com.example.multifunctionalchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages="com.example.multifunctionalchat")
@EnableTransactionManagement
@EntityScan(basePackages="com.example.multifunctionalchat.domain")
public class MultiFunctionalChatApplication {


	public static void main(String[] args) {
		SpringApplication.run(MultiFunctionalChatApplication.class, args);
	}

}
