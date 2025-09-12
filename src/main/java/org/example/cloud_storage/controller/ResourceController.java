package org.example.cloud_storage.controller;

import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.ResourceResponseDto;
import org.example.cloud_storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResourceInfo(@RequestParam String path, @AuthenticationPrincipal UserDetails user) {
        storageService.deleteResource(path, user.getUsername());
    }


}
