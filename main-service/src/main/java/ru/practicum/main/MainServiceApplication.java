package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Главный класс приложения ExploreWithMe Main Service.
 * <p>
 * Этот сервис отвечает за основную бизнес-логику приложения:
 * <ul>
 *     <li>Управление событиями (создание, редактирование, публикация)</li>
 *     <li>Управление пользователями и категориями</li>
 *     <li>Обработка заявок на участие в событиях</li>
 *     <li>Интеграция со Stats Service для сбора статистики просмотров</li>
 * </ul>
 * <p>
 * Сканирует пакеты:
 * <ul>
 *     <li>{@code ru.practicum.main} - основные компоненты сервиса</li>
 *     <li>{@code ru.practicum.client} - клиент для Stats Service</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ru.practicum.client.StatsClient
 */
@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.main", "ru.practicum.client"})
public class MainServiceApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}
