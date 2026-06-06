package ru.practicum.main.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.main.user.validation.ValidEmailDomainLabel;

/**
 * User entity.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique user identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User name.
     */
    @NotBlank
    @Size(min = 2, max = 250)
    @Column(nullable = false, length = 250)
    private String name;

    /**
     * User email (unique).
     */
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    @ValidEmailDomainLabel
    @Column(nullable = false, unique = true, length = 254)
    private String email;
}
