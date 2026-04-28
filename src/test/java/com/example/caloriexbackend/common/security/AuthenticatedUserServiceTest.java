package com.example.caloriexbackend.common.security;

import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @Test
    void findUserShouldNotLinkAccountsByMatchingEmail() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "google-oauth2|123",
                "user@example.com",
                true,
                "John Doe"
        );

        when(userRepository.findByAuth0Id("google-oauth2|123")).thenReturn(Optional.empty());

        Optional<User> actual = ReflectionTestUtils.invokeMethod(
                authenticatedUserService,
                "findUser",
                authenticatedUser
        );

        assertNotNull(actual);
        verify(userRepository).findByAuth0Id("google-oauth2|123");
        verify(userRepository, never()).findByEmailIgnoreCase("user@example.com");
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }
}
