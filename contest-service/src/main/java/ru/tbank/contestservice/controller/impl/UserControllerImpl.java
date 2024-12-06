package ru.tbank.contestservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.contestservice.controller.UserController;
import ru.tbank.contestservice.dto.user.UserDTO;
import ru.tbank.contestservice.dto.user.UserRegisterRequestDTO;
import ru.tbank.contestservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public void createUser(UserRegisterRequestDTO userRegisterRequestDTO) {
        userService.createUser(
                userRegisterRequestDTO.username(),
                userRegisterRequestDTO.password(),
                userRegisterRequestDTO.email()
        );
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        userService.updateUser(
                userDTO.username(),
                userDTO.email()
        );
    }

}
