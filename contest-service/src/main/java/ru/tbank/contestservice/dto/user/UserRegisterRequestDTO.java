package ru.tbank.contestservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import ru.tbank.contestservice.utils.validation.annotations.PasswordConstraint;

public record UserRegisterRequestDTO(
        @NotBlank(message = "username cannot be empty")
        @Length(max = 255)
        @JsonProperty("username")
        String username,

        @Email
        @Length(max = 255)
        @JsonProperty("email")
        String email,

        @NotBlank(message = "password cannot be empty")
        @PasswordConstraint
        @JsonProperty("password")
        String password
) {
}
