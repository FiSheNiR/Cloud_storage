package org.example.cloud_storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto signUp(@RequestBody UserRequestDto userRequestDto) {
        return userService.register(userRequestDto);
    }

    @PostMapping("sign-in")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto signIn(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(userRequestDto, request,response);
    }

    @PostMapping("sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut() {
        userService.logout();
    }
}
