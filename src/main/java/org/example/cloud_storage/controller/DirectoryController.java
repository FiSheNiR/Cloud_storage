package org.example.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/directory")
public class DirectoryController {

    private final StorageService storageService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponseDto> getDirectoryInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user) {
        return storageService.getDirectoryInfo(path, user.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponseDto createDirectory(@RequestParam String path, @AuthenticationPrincipal UserDetails user) {
        return storageService.createDirectory(path, user.getUsername());
    }
}
