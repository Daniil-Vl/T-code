package ru.tbank.contestservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import ru.tbank.contestservice.model.entities.UserEntity;

public record UserDTO(
        @NotBlank
        @JsonProperty("username")
        String username,

        @JsonProperty("email")
        String email
) {
    public static UserDTO from(UserEntity userEntity) {
        return new UserDTO(
                userEntity.getUsername(),
                userEntity.getEmail()
        );
    }
}
