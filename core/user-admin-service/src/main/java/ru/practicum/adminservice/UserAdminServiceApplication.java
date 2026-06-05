package ru.practicum.adminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.adminservice",
        "ru.practicum.main.exception",
        "ru.practicum.main.user",
        "ru.practicum.main.util"
})
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "ru.practicum.main")
@EntityScan(basePackages = "ru.practicum.main")
public class UserAdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAdminServiceApplication.class, args);
    }
}
