package org.example.cloud_storage.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DirectorySwagger {

    @Operation(
            tags = {"Directory"},
            summary = "List directory contents",
            description = "Returns non-recursive list of resources located in the specified folder. " +
                    "If path is empty, returns root folder contents.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Directory path. Empty string means root.",
                            required = true,
                            allowEmptyValue = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Directory contents retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ResourceResponseDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or missing path",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User is not authenticated",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Directory does not exist",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    public List<ResourceResponseDto> getDirectoryInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Directory"},
            summary = "Create an empty directory",
            description = "Creates a new empty directory at the specified path.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Path to the new directory to be created.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Directory created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or missing path to the new directory",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User is not authenticated",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Parent directory does not exist",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Directory already exists",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    public ResourceResponseDto createDirectory(@RequestParam String path, @AuthenticationPrincipal UserDetails user);
}
