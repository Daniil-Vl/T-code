package ru.tbank.contestservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.contestservice.dao.jpa.UserRepository;
import ru.tbank.contestservice.dto.user.UserDTO;
import ru.tbank.contestservice.exception.user.UsernameOccupiedException;
import ru.tbank.contestservice.exception.user.UserNotFoundException;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(String username, String password, String email) {
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameOccupiedException(String.format("Username %s is already taken", username));
        }

        UserEntity savedEntity = userRepository.save(userEntity);

        log.info("Create user with username {}", username);
        return UserDTO.from(savedEntity);
    }

    @Override
    @Transactional
    public UserDTO updateUser(String username, String email) {
        log.info("Update user with username {}", username);
        UserEntity authenticatedUser = getAuthenticatedUser();

        if (username != null) {
            authenticatedUser.setUsername(username);
        }
        if (email != null) {
            authenticatedUser.setEmail(email);
        }

        UserEntity savedEntity = userRepository.save(authenticatedUser);
        return UserDTO.from(savedEntity);
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Cannot get authenticated user from context");
        }

        return (UserEntity) authentication.getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with username {} not found", username);
                    return new UserNotFoundException(String.format("User with username %s not found", username));
                });
    }
}
