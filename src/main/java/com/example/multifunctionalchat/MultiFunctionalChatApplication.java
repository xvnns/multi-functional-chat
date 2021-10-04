package com.example.multifunctionalchat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiFunctionalChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiFunctionalChatApplication.class, args);
	}

}
