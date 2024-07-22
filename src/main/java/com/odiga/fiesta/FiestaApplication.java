package com.odiga.fiesta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class FiestaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiestaApplication.class, args);
	}

}
