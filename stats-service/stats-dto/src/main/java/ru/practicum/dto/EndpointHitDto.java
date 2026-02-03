package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for endpoint hit data.
 * <p>
 * Review fixes applied:
 * - Removed 'id' field (not needed for DTO)
 * - Date format extracted to DateTimeFormatConstants.DATE_TIME_PATTERN
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank(message = "App cannot be blank")
    private String app;

    @NotBlank(message = "URI cannot be blank")
    private String uri;

    @NotBlank(message = "IP cannot be blank")
    private String ip;

    @NotNull(message = "Timestamp cannot be null")
    @JsonFormat(pattern = DateTimeFormatConstants.DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}