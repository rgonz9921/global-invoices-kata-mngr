package com.project_kata.global_invoices_kata_mngr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GlobalInvoicesKataMngrApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlobalInvoicesKataMngrApplication.class, args);
	}

}
