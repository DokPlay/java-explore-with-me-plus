package ru.practicum.main.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User DTO for admin operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    /**
     * Identifier.
     */
    private Long id;

    /**
     * Email.
     */
    private String email;

    /**
     * Name.
     */
    private String name;
}
