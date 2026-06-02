package ru.practicum.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.eventservice",
        "ru.practicum.main.category",
        "ru.practicum.main.event",
        "ru.practicum.main.exception",
        "ru.practicum.main.location",
        "ru.practicum.main.moderation",
        "ru.practicum.main.rating",
        "ru.practicum.main.user",
        "ru.practicum.main.util",
        "ru.practicum.client"
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.practicum.eventservice.client")
@EnableJpaRepositories(basePackages = "ru.practicum.main")
@EntityScan(basePackages = "ru.practicum.main")
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}
