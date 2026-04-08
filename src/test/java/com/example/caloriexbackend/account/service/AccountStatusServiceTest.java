package com.example.caloriexbackend.account.service;

import com.example.caloriexbackend.account.payload.AccountStatusResponse;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountStatusServiceTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountStatusService accountStatusService;

    @Test
    void getProfileStatusShouldReturnTrueWhenProfileExists() {
        when(authenticatedUserService.getAuth0Id()).thenReturn("auth0|123");
        when(userRepository.existsByAuth0Id("auth0|123")).thenReturn(true);

        AccountStatusResponse actual = accountStatusService.getProfileStatus();

        assertEquals(true, actual.hasProfile());
        verify(userRepository).existsByAuth0Id("auth0|123");
    }

    @Test
    void getProfileStatusShouldReturnFalseWhenProfileDoesNotExist() {
        when(authenticatedUserService.getAuth0Id()).thenReturn("auth0|123");
        when(userRepository.existsByAuth0Id("auth0|123")).thenReturn(false);

        AccountStatusResponse actual = accountStatusService.getProfileStatus();

        assertEquals(false, actual.hasProfile());
        verify(userRepository).existsByAuth0Id("auth0|123");
    }
}
