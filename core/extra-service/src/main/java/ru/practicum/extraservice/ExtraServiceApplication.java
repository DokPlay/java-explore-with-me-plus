package ru.practicum.extraservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.extraservice",
        "ru.practicum.main.category",
        "ru.practicum.main.comment",
        "ru.practicum.main.compilation",
        "ru.practicum.main.event",
        "ru.practicum.main.exception",
        "ru.practicum.main.location",
        "ru.practicum.main.moderation",
        "ru.practicum.main.rating",
        "ru.practicum.main.subscription",
        "ru.practicum.main.user",
        "ru.practicum.main.util",
        "ru.practicum.client"
})
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "ru.practicum.main")
@EntityScan(basePackages = "ru.practicum.main")
public class ExtraServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtraServiceApplication.class, args);
    }
}
