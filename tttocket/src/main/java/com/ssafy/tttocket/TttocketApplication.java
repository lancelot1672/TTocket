package com.ssafy.tttocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TttocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(TttocketApplication.class, args);
	}

}
