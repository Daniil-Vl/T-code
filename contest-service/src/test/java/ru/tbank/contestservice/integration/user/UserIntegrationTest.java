package ru.tbank.contestservice.integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.contestservice.dao.jpa.UserRepository;
import ru.tbank.contestservice.dto.user.UserDTO;
import ru.tbank.contestservice.dto.user.UserRegisterRequestDTO;
import ru.tbank.contestservice.integration.AbstractIntegrationTest;
import ru.tbank.contestservice.model.entities.UserEntity;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Register user success
    @Test
    void givenUserDto_whenCreateUser_thenUserCreated() throws Exception {
        // Assign
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("username", "email@gmail.com", "password123!");
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // Assert
        List<UserEntity> users = userRepository.findAll();
        Assertions.assertEquals(1, users.size());
        UserEntity user = users.getFirst();
        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertEquals("email@gmail.com", user.getEmail());
    }

    // Register user failed: bad request (username occupied)
    @Test
    void givenUserDto_whenCreateUserAndUsernameOccupied_thenReturnBadRequest() throws Exception {
        // Assign
        userRepository.save(
                UserEntity.builder()
                        .username("username")
                        .password("password")
                        .build()
        );
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("username", "email@gmail.com", "password123!");
        String requestBody = objectMapper.writeValueAsString(request);

        // Act + Assert
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Register user failed: bad request (blank username)
    @Test
    void givenUserDto_whenCreateUserWithBlankUsername_thenReturnBadRequest() throws Exception {
        // Assign
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("", "email@gmail.com", "password123!");
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Register user failed: bad request (weak password)
    @Test
    void givenUserDto_whenCreateUserWithWeakPassword_thenReturnBadRequest() throws Exception {
        // Assign
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("username", "email@gmail.com", "password123");
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Update user success
    @Test
    void givenUserDto_whenUpdateUser_thenUserUpdated() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserDTO userDTO = new UserDTO("newUserName", null);
        String requestBody = objectMapper.writeValueAsString(userDTO);

        // Act
        mockMvc.perform(
                        patch(BASE_URL)
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        List<UserEntity> users = userRepository.findAll();
        Assertions.assertEquals(1, users.size());
        UserEntity user = users.getFirst();
        Assertions.assertEquals("newUserName", user.getUsername());
    }

    // Update user failed: user unauthenticated
    @Test
    void givenUserDto_whenUpdateUserAndUnauthenticated_thenReturnUnauthorized() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserDTO userDTO = new UserDTO("newUserName", null);
        String requestBody = objectMapper.writeValueAsString(userDTO);

        // Act
        mockMvc.perform(
                        patch(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Assert
        List<UserEntity> users = userRepository.findAll();
        Assertions.assertEquals(1, users.size());
        UserEntity user = users.getFirst();
        Assertions.assertEquals("username", user.getUsername());
    }

    // Update user failed: username is blank
    @Test
    void givenUserDtoWithBlankUsername_whenUpdateUser_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserDTO userDTO = new UserDTO("", null);
        String requestBody = objectMapper.writeValueAsString(userDTO);

        // Act
        mockMvc.perform(
                        patch(BASE_URL)
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Assert
        List<UserEntity> users = userRepository.findAll();
        Assertions.assertEquals(1, users.size());
        UserEntity user = users.getFirst();
        Assertions.assertEquals("username", user.getUsername());
    }

}
