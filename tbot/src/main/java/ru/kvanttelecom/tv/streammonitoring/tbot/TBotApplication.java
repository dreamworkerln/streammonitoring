package ru.kvanttelecom.tv.streammonitoring.tbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.kvanttelecom.tv.streammonitoring")
public class TBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TBotApplication.class, args);
	}

}
