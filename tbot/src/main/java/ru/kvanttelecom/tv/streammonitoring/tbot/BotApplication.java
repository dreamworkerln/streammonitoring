package ru.kvanttelecom.tv.streammonitoring.tbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kvanttelecom.tv.streammonitoring.utils.configurations.annotations.MultimoduleSpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.kvanttelecom.tv.streammonitoring")
//@MultimoduleSpringBootApplication
public class BotApplication {
	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}

}
