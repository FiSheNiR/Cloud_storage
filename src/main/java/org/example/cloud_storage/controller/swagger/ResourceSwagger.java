package org.example.cloud_storage.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceSwagger {
    @Operation(
            tags = {"Resource"},
            summary = "Get resource metadata",
            description = "Returns metadata of a file or directory by its full path. " +
                    "Path must be URL-encoded. Directories must end with '/'.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full URL-encoded path to the resource. Directories must end with '/'.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resource metadata retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing path"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResourceResponseDto getResourceInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Resource"},
            summary = "Download a resource",
            description = "Downloads a file as binary stream (Content-Type: application/octet-stream). " +
                    "If the resource is a directory, returns a ZIP archive of its contents. " +
                    "Path must be URL-encoded and end with '/' for directories.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full URL-encoded path to the resource. Directories must end with '/'.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File or ZIP archive downloaded successfully",
                            content = @Content(mediaType = "application/octet-stream")
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing path"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public void downloadResource(@RequestParam String path, HttpServletResponse response, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Resource"},
            summary = "Move or rename a resource",
            description = "Moves or renames a file/directory. " +
                    "Both 'from' and 'to' must be full URL-encoded paths. " +
                    "Directory paths must end with '/'.",
            parameters = {
                    @Parameter(
                            name = "from",
                            description = "Current full URL-encoded path of the resource",
                            required = true,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "to",
                            description = "New full URL-encoded path for the resource. Must end with '/' if target is directory.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resource moved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResourceResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing path(s)"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Source resource not found"),
                    @ApiResponse(responseCode = "409", description = "Target resource already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResourceResponseDto moveResource(@RequestParam String from, @RequestParam String to, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Resource"},
            summary = "Search resources by query",
            description = "Performs a case-insensitive search for resources by name. " +
                    "Query must be URL-encoded.",
            parameters = {
                    @Parameter(
                            name = "query",
                            description = "Search term (URL-encoded)",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search completed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ResourceResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing search query"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public List<ResourceResponseDto> searchResource(@RequestParam String query, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Resource"},
            summary = "Delete a resource",
            description = "Deletes a file or directory by its full path. " +
                    "Path must be URL-encoded. Directories must end with '/'.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full URL-encoded path to the resource. Directories must end with '/'.",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing path"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public void deleteResourceInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user);

    @Operation(
            tags = {"Resource"},
            summary = "Upload one or more files",
            description = "Uploads files to the specified directory. " +
                    "The 'path' is the target directory (URL-encoded, must end with '/'). " +
                    "Uploaded filenames may contain subdirectories (e.g., 'folder/file.txt'), " +
                    "which will be created recursively in storage.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Target directory path (URL-encoded, must end with '/')",
                            required = true,
                            in = ParameterIn.QUERY
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "One or more files to upload",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "array", implementation = MultipartFile.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Files uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ResourceResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or path"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "409", description = "File already exists at target location"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public List<ResourceResponseDto> uploadResource(@RequestParam String path, @RequestPart("object") List<MultipartFile> files, @AuthenticationPrincipal UserDetails user);
}
