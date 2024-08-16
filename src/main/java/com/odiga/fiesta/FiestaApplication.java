package com.odiga.fiesta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
@EnableAsync
public class FiestaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiestaApplication.class, args);
	}

}
