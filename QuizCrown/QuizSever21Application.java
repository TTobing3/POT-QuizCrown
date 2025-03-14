package com.example.quizSever21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuizSever21Application {

	public static void main(String[] args) {
		SpringApplication.run(QuizSever21Application.class, args);
	}

}
