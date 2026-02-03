package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Starts the ExploreWithMe Main Service application.
 * <p>
 * The service is responsible for the core business logic: managing events, users,
 * categories, and participation requests, as well as integrating with Stats Service.
 * <p>
 * Scanned packages:
 * <ul>
 *     <li>{@code ru.practicum.main} — main service components</li>
 *     <li>{@code ru.practicum.client} — Stats Service client</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.main", "ru.practicum.client"})
public class MainServiceApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}
