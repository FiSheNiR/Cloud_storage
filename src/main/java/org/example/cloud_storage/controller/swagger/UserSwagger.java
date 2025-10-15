package org.example.cloud_storage.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.cloud_storage.dto.ErrorResponseDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserSwagger {
    @Operation(
            tags = {"Authorization"},
            summary = "Get current user info",
            description = "Returns basic information about the currently authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User info retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – user is not authenticated",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                    )
            }
    )
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal UserDetails user);
}
