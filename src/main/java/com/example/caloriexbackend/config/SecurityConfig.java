package com.example.caloriexbackend.config;

import com.example.caloriexbackend.common.security.CsrfCookieFilter;
import com.example.caloriexbackend.common.security.OAuth2LoginFailureHandler;
import com.example.caloriexbackend.common.security.OAuth2LoginSuccessHandler;
import com.example.caloriexbackend.common.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AppAuthProperties.class)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean secureSessionCookie;

    @Value("${server.servlet.session.cookie.same-site:none}")
    private String sameSite;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    OAuth2LoginSuccessHandler successHandler,
                                    OAuth2LoginFailureHandler failureHandler) throws Exception {
        PathPatternRequestMatcher.Builder pathPatterns = PathPatternRequestMatcher.withDefaults();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        .ignoringRequestMatchers(
                                pathPatterns.matcher("/api/auth/login"),
                                pathPatterns.matcher("/api/auth/logout"),
                                pathPatterns.matcher("/oauth2/**"),
                                pathPatterns.matcher("/login/oauth2/**")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/me", "/api/auth/csrf").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/auth/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                pathPatterns.matcher("/api/**")
                        )
                        .defaultAccessDeniedHandlerFor(
                                (request, response, accessDeniedException) -> {
                                    logAccessDenied(request, accessDeniedException);
                                    response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
                                },
                                pathPatterns.matcher("/api/**")
                        )
                )
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .secure(secureSessionCookie)
                .sameSite(sameSite));
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList()));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void logAccessDenied(jakarta.servlet.http.HttpServletRequest request,
                                 org.springframework.security.access.AccessDeniedException exception) {
        if (!"/api/user/profile".equals(request.getRequestURI())) {
            return;
        }

        String csrfCookie = extractXsrfCookieValue(request);
        String csrfHeader = request.getHeader("X-XSRF-TOKEN");
        String sessionId = request.getRequestedSessionId();
        Object csrfAttribute = request.getAttribute(CsrfToken.class.getName());
        String expectedToken = csrfAttribute instanceof CsrfToken csrfToken ? csrfToken.getToken() : null;

        if (exception instanceof InvalidCsrfTokenException) {
            log.warn("Access denied for {} {} due to invalid CSRF token. sessionId={}, headerToken={}, cookieToken={}, expectedToken={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    sessionId,
                    csrfHeader,
                    csrfCookie,
                    expectedToken);
            return;
        }

        if (exception instanceof MissingCsrfTokenException) {
            log.warn("Access denied for {} {} due to missing CSRF token. sessionId={}, headerToken={}, cookieToken={}, expectedToken={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    sessionId,
                    csrfHeader,
                    csrfCookie,
                    expectedToken);
            return;
        }

        log.warn("Access denied for {} {}. sessionId={}, headerToken={}, cookieToken={}, expectedToken={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                sessionId,
                csrfHeader,
                csrfCookie,
                expectedToken,
                exception.getMessage());
    }

    private String extractXsrfCookieValue(jakarta.servlet.http.HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("XSRF-TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
