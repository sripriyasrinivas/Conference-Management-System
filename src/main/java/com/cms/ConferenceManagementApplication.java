package com.cms;

import com.cms.service.DataInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConferenceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConferenceManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(DataInitializerService dataInitializerService) {
        return args -> dataInitializerService.initializeData();
    }
}
