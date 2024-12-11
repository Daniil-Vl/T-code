package ru.tbank.contestservice.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tbank.contestservice.dao.jpa.UserRepository;
import ru.tbank.contestservice.dto.user.UserDTO;
import ru.tbank.contestservice.exception.user.UserNotFoundException;
import ru.tbank.contestservice.exception.user.UsernameOccupiedException;
import ru.tbank.contestservice.model.entities.UserEntity;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // Create user
    @Test
    void givenUserDate_whenCreateUser_thenUserCreated() {
        // Assign
        String username = "user";
        String password = "password";
        String email = "email";
        UserDTO expectedUserDTO = new UserDTO(username, email);

        Mockito.when(
                userRepository.existsByUsername(username)
        ).thenReturn(Boolean.FALSE);

        Mockito.when(
                userRepository.save(Mockito.any(UserEntity.class))
        ).thenReturn(
                UserEntity.builder().username(username).email(email).build()
        );

        // Act
        UserDTO actualUserDTO = userService.createUser(username, password, email);

        // Assert
        Assertions.assertEquals(expectedUserDTO, actualUserDTO);
        Mockito.verify(userRepository).existsByUsername(username);
        Mockito.verify(userRepository).save(Mockito.any(UserEntity.class));
    }

    // Occupied username
    @Test
    void givenOccupiedUsername_whenCreateUser_thenUserNicknameOccupiedExceptionThrown() {
        // Assign
        String username = "user";
        String password = "password";
        String email = "email";
        Mockito.when(
                userRepository.existsByUsername(username)
        ).thenReturn(Boolean.TRUE);

        // Act + Assert
        Assertions.assertThrows(
                UsernameOccupiedException.class,
                () -> userService.createUser(username, password, email)
        );
    }

    // Load user by username
    @Test
    void givenExistingUser_whenLoadUserByUsername_thenUserFound() {
        // Assign
        String username = "user";
        UserEntity userEntity = Mockito.mock(UserEntity.class);

        Mockito.when(
                userRepository.findByUsername(username)
        ).thenReturn(
                Optional.of(userEntity)
        );

        // Act + Assert
        Assertions.assertDoesNotThrow(
                () -> userService.loadUserByUsername(username)
        );
    }

    // Load non-existent user by username
    @Test
    void givenNonExistingUser_whenLoadUserByUsername_thenUserNotFoundExceptionThrown() {
        // Assign
        String username = "user";

        Mockito.when(
                userRepository.findByUsername(username)
        ).thenReturn(
                Optional.empty()
        );

        // Act + Assert
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.loadUserByUsername(username)
        );
    }

}
