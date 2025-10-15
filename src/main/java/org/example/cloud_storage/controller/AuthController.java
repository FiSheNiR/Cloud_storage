package org.example.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloud_storage.controller.swagger.AuthSwagger;
import org.example.cloud_storage.dto.ErrorResponseDto;
import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthSwagger {

    private final UserService userService;

    @PostMapping("sign-in")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto signIn(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(userRequestDto, request,response);
    }

    @PostMapping("sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto signUp(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        userService.register(userRequestDto);
        return userService.login(userRequestDto, request, response);
    }

    @PostMapping("sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut() {
        userService.logout();
    }
}
