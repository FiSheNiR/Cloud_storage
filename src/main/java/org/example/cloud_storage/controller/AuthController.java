package org.example.cloud_storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AuthController {

    private final UserService userService;

    @Operation(
            tags = {"Authorization"},
            summary = "Register new user",
            description = "Registers a new user and returns basic user information",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Username already exists",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    )
            }
    )
    @PostMapping("sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto signUp(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        userService.register(userRequestDto);
        return userService.login(userRequestDto, request, response);
    }

    @Operation(
            tags = {"Authorization"},
            summary = "User login",
            description = "Authenticates user with provided credentials and returns basic user information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Incorrect username or password",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    )
            }
    )
    @PostMapping("sign-in")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto signIn(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(userRequestDto, request,response);
    }

    @Operation(
            tags = {"Authorization"},
            summary = "User logout",
            description = "User logout",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Logout successful"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized user tries to logout",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    )
            }
    )
    @PostMapping("sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut() {
        userService.logout();
    }
}
