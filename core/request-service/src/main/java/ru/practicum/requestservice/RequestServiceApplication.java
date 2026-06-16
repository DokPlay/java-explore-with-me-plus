package ru.practicum.requestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.requestservice",
        "ru.practicum.main.exception",
        "ru.practicum.main.request",
        "ru.practicum.main.util",
        "ru.practicum.client"
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.practicum.requestservice.client")
@EnableJpaRepositories(basePackages = "ru.practicum.main")
@EntityScan(basePackages = "ru.practicum.main")
public class RequestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}
