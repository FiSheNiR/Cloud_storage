package org.example.cloud_storage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ErrorResponseDto;
import org.example.cloud_storage.exception.UnautorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticatedLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        if (authentication == null || !authentication.isAuthenticated()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ErrorResponseDto("Unauthorized user can not logout"));
        } else {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}
