package ru.tbank.contestservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.tbank.contestservice.dto.user.UserDTO;
import ru.tbank.contestservice.model.entities.UserEntity;

public interface UserService extends UserDetailsService {
    /**
     * Create new user
     *
     * @param username
     * @param password
     * @param email
     * @return
     */
    UserDTO createUser(String username, String password, String email);

    /**
     * Update user`s username and\or email
     *
     * @param username
     * @param email
     * @return
     */
    UserDTO updateUser(String username, String email);

    /**
     * Retrieve current authenticated user from SecurityContext.
     * This method must be called only, when processing requests on protected endpoints, which requires authentication.
     *
     * @return
     * @throws IllegalStateException, if user unauthenticated
     */
    UserEntity getAuthenticatedUser() throws IllegalStateException;
}
