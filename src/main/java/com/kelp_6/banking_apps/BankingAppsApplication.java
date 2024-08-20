package com.kelp_6.banking_apps;

import com.kelp_6.banking_apps.setup.DatabaseSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class BankingAppsApplication {

	public static void main(String[] args) {
		DatabaseSeeder seeder = SpringApplication
				.run(BankingAppsApplication.class, args)
				.getBean(DatabaseSeeder.class);

		seeder.setup();
	}

}
