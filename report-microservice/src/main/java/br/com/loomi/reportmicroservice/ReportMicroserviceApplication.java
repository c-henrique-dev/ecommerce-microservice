package br.com.loomi.reportmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReportMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportMicroserviceApplication.class, args);
	}

}
