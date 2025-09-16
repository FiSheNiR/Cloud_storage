package org.example.cloud_storage.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/resource")
public class ResourceController {

    private final StorageService storageService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponseDto getResourceInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user) {
        return storageService.getResourceInfo(path, user.getUsername());
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    public void downloadResource(@RequestParam String path, HttpServletResponse response, @AuthenticationPrincipal UserDetails user) {
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        if (decodedPath.endsWith("/")) {
            storageService.downloadDirectory(decodedPath, response, user.getUsername());
        } else {
            storageService.downloadFile(decodedPath, response, user.getUsername());
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResourceInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user) {
        storageService.deleteResource(path, user.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceResponseDto> uploadResource(@RequestParam String path,@RequestPart("object") List<MultipartFile> files, @AuthenticationPrincipal UserDetails user) {
        return storageService.uploadResource(path,files,user.getUsername());
    }
}
