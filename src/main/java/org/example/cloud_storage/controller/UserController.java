package org.example.cloud_storage.controller;

import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    @GetMapping("/me")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return new UserResponseDto(currentUser.getUsername());
    }
}
