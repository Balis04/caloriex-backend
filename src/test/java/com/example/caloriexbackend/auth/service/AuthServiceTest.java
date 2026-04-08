package com.example.caloriexbackend.auth.service;

import com.example.caloriexbackend.auth.payload.AuthMeResponse;
import com.example.caloriexbackend.common.enums.UserRole;
import com.example.caloriexbackend.common.security.AuthenticatedUser;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.config.AppAuthProperties;
import com.example.caloriexbackend.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        AppAuthProperties authProperties = new AppAuthProperties();
        authProperties.setFrontendBaseUrl("https://frontend.example.com");
        authProperties.setPostLogoutPath("/logged-out");
        authProperties.setAuth0LogoutEnabled(true);

        authService = new AuthService(authenticatedUserService, authProperties);
        ReflectionTestUtils.setField(authService, "auth0IssuerUri", "https://tenant.eu.auth0.com/");
        ReflectionTestUtils.setField(authService, "auth0ClientId", "client-123");
        ReflectionTestUtils.setField(authService, "sessionCookieName", "CALORIEX_SESSION");
        ReflectionTestUtils.setField(authService, "secureSessionCookie", true);
        ReflectionTestUtils.setField(authService, "sameSite", "None");
    }

    @Test
    void getCurrentUserShouldReturnAnonymousResponseWhenNoAuthenticatedUserExists() {
        when(authenticatedUserService.findAuthenticatedUser()).thenReturn(Optional.empty());

        AuthMeResponse actual = authService.getCurrentUser();

        assertFalse(actual.authenticated());
        assertNull(actual.userId());
        assertNull(actual.auth0Id());
        assertNull(actual.email());
        assertFalse(actual.emailVerified());
        assertNull(actual.fullName());
        assertNull(actual.role());
        assertFalse(actual.hasProfile());
    }

    @Test
    void getCurrentUserShouldPreferLocalProfileWhenItExists() {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser("auth0|abc", "principal@example.com", true, "Principal Name");
        User localUser = new User();
        localUser.setId(userId);
        localUser.setEmail("local@example.com");
        localUser.setFullName("Local Name");
        localUser.setRole(UserRole.COACH);

        when(authenticatedUserService.findAuthenticatedUser()).thenReturn(Optional.of(principal));
        when(authenticatedUserService.findUser()).thenReturn(Optional.of(localUser));

        AuthMeResponse actual = authService.getCurrentUser();

        assertTrue(actual.authenticated());
        assertEquals(userId, actual.userId());
        assertEquals("auth0|abc", actual.auth0Id());
        assertEquals("local@example.com", actual.email());
        assertTrue(actual.emailVerified());
        assertEquals("Local Name", actual.fullName());
        assertEquals("COACH", actual.role());
        assertTrue(actual.hasProfile());
    }

    @Test
    void getCurrentUserShouldFallbackToPrincipalDataWhenLocalProfileIsMissing() {
        AuthenticatedUser principal = new AuthenticatedUser("auth0|abc", "principal@example.com", false, "Principal Name");

        when(authenticatedUserService.findAuthenticatedUser()).thenReturn(Optional.of(principal));
        when(authenticatedUserService.findUser()).thenReturn(Optional.empty());

        AuthMeResponse actual = authService.getCurrentUser();

        assertTrue(actual.authenticated());
        assertNull(actual.userId());
        assertEquals("auth0|abc", actual.auth0Id());
        assertEquals("principal@example.com", actual.email());
        assertFalse(actual.emailVerified());
        assertEquals("Principal Name", actual.fullName());
        assertNull(actual.role());
        assertFalse(actual.hasProfile());
    }

    @Test
    void getAuth0LogoutUrlShouldReturnNullWhenLogoutIsDisabled() {
        AppAuthProperties authProperties = new AppAuthProperties();
        authProperties.setAuth0LogoutEnabled(false);
        AuthService disabledAuthService = new AuthService(authenticatedUserService, authProperties);
        ReflectionTestUtils.setField(disabledAuthService, "auth0IssuerUri", "https://tenant.eu.auth0.com/");
        ReflectionTestUtils.setField(disabledAuthService, "auth0ClientId", "client-123");

        String actual = disabledAuthService.getAuth0LogoutUrl();

        assertNull(actual);
    }
}

