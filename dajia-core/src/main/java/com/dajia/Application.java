package com.dajia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
		"com.dajia.repository",
		"com.dajia.service",
		"com.dajia.cache",
		"com.dajia.controller",
		"com.dajia.filter",
		"com.dajia.task",
		"com.dajia"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
