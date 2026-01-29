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
 * Error response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    /**
     * List of error messages.
     */
    private List<String> errors;

    /**
     * Short error message.
     */
    private String message;

    /**
     * General description of the error cause.
     */
    private String reason;

    /**
     * HTTP response status code.
     */
    private String status;

    /**
     * Response generation timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
