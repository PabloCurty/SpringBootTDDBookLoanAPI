package com.curty.libraryAPI;

import com.curty.libraryAPI.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public CommandLineRunner runner(){
		return args -> {
			List<String> emails = Arrays.asList("library.api-20a7df@inbox.mailtrap.io");
			emailService.sendMails("Email service test", emails);
			System.out.println("Emails enviados");
		};
	}

	//Singleton
	// Where you need a modelMapper,
	// just inject it into the class and it will always provide the same instance.
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Scheduled(cron = "0 10 17 1/1 * ?")
	public void testScheduling(){
		System.out.println("Agendamento de tarefas funcionando com sucesso");
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
