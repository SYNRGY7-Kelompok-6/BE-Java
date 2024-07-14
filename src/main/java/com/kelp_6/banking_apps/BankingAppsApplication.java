package com.kelp_6.banking_apps;

import com.kelp_6.banking_apps.setup.DatabaseSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingAppsApplication {

	public static void main(String[] args) {
		DatabaseSeeder seeder = SpringApplication
				.run(BankingAppsApplication.class, args)
				.getBean(DatabaseSeeder.class);

		seeder.setup();
	}

}
