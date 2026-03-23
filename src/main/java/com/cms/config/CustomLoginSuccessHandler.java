package com.cms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_ATTENDEE");

        String redirectUrl = switch (role) {
            case "ROLE_ADMIN"    -> "/admin/dashboard";
            case "ROLE_AUTHOR"   -> "/author/dashboard";
            case "ROLE_REVIEWER" -> "/reviewer/dashboard";
            case "ROLE_SPEAKER"  -> "/speaker/dashboard";
            case "ROLE_ATTENDEE" -> "/attendee/dashboard";
            default              -> "/dashboard";
        };

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
