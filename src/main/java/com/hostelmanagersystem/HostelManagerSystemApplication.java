package com.hostelmanagersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hostelmanagersystem")
public class HostelManagerSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostelManagerSystemApplication.class, args);
	}

}
