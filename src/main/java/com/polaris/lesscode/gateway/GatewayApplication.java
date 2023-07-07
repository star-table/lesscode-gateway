package com.polaris.lesscode.gateway;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
@EnableFeignClients
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		Sentry.getStoredClient().setEnvironment(System.getenv("SERVER_ENVIROMENT"));
		SpringApplication.run(GatewayApplication.class, args);
	}

}
