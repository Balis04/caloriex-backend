package com.example.caloriexbackend.common.security;

import com.example.caloriexbackend.config.AppAuthProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AppAuthProperties authProperties;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final CsrfTokenRepository csrfTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {
            authorizedClientRepository.removeAuthorizedClient(
                    oauth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                    authentication,
                    request,
                    response
            );
        }

        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, authProperties.getPostLoginRedirectUrl());
    }
}
