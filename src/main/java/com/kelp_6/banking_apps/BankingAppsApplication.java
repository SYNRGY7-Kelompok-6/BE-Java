package com.kelp_6.banking_apps;

import com.kelp_6.banking_apps.setup.DatabaseSeeder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(servers = { @Server(url = "/api/v1.0/", description = "Default Server URL") })
public class BankingAppsApplication {

	public static void main(String[] args) {
		DatabaseSeeder seeder = SpringApplication
				.run(BankingAppsApplication.class, args)
				.getBean(DatabaseSeeder.class);

		seeder.setup();
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+7:00"));
	}
}
