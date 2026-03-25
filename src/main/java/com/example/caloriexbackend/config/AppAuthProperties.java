package com.example.caloriexbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {

    private String frontendBaseUrl = "http://localhost:5173";
    private String postLoginPath = "/auth/callback";
    private String postLogoutPath = "/";
    private boolean auth0LogoutEnabled = true;

    public String getPostLoginRedirectUrl() {
        return frontendBaseUrl + normalizePath(postLoginPath);
    }

    public String getPostLogoutRedirectUrl() {
        return frontendBaseUrl + normalizePath(postLogoutPath);
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "/";
        }

        return path.startsWith("/") ? path : "/" + path;
    }
}
