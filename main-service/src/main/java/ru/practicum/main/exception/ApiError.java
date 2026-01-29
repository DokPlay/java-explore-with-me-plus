package ru.practicum.main.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO ошибки API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    /**
     * Список ошибок (стектрейсы или описания).
     */
    private List<String> errors;

    /**
     * Сообщение об ошибке.
     */
    private String message;

    /**
     * Общее описание причины ошибки.
     */
    private String reason;

    /**
     * Код статуса HTTP-ответа.
     */
    private String status;

    /**
     * Дата и время ошибки.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
