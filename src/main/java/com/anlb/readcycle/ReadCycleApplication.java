package com.anlb.readcycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ReadCycleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadCycleApplication.class, args);
	}

}
