package com.example.caloriexbackend.auth.service;

import com.example.caloriexbackend.auth.payload.AuthMeResponse;
import com.example.caloriexbackend.common.security.AuthenticatedUser;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.config.AppAuthProperties;
import com.example.caloriexbackend.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticatedUserService authenticatedUserService;
    private final AppAuthProperties authProperties;

    @Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}")
    private String auth0IssuerUri;

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String auth0ClientId;

    @Value("${server.servlet.session.cookie.name:CALORIEX_SESSION}")
    private String sessionCookieName;

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean secureSessionCookie;

    @Value("${server.servlet.session.cookie.same-site:none}")
    private String sameSite;

    public AuthMeResponse getCurrentUser() {
        Optional<AuthenticatedUser> authenticatedUser = authenticatedUserService.findAuthenticatedUser();
        if (authenticatedUser.isEmpty()) {
            return new AuthMeResponse(false, null, null, null, false, null, null, false);
        }

        AuthenticatedUser principal = authenticatedUser.get();
        User localUser = authenticatedUserService.findUser().orElse(null);

        return new AuthMeResponse(
                true,
                localUser != null ? localUser.getId() : null,
                principal.auth0Id(),
                localUser != null ? localUser.getEmail() : principal.email(),
                principal.emailVerified(),
                localUser != null ? localUser.getFullName() : principal.fullName(),
                localUser != null ? localUser.getRole().name() : null,
                localUser != null
        );
    }

    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie(sessionCookieName, true).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie("XSRF-TOKEN", false).toString());
    }

    private ResponseCookie expiredCookie(String name, boolean httpOnly) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .httpOnly(httpOnly)
                .secure(secureSessionCookie)
                .sameSite(sameSite)
                .build();
    }

    public String getAuth0LogoutUrl() {
        if (!authProperties.isAuth0LogoutEnabled()) {
            return null;
        }

        String issuerBaseUrl = auth0IssuerUri.endsWith("/")
                ? auth0IssuerUri.substring(0, auth0IssuerUri.length() - 1)
                : auth0IssuerUri;

        return UriComponentsBuilder
                .fromUriString(issuerBaseUrl + "/v2/logout")
                .queryParam("client_id", auth0ClientId)
                .queryParam("returnTo", authProperties.getPostLogoutRedirectUrl())
                .build()
                .encode()
                .toUriString();
    }
}
