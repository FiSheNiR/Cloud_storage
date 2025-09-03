package org.example.cloud_storage.service;

import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.mapper.UserMapper;
import org.example.cloud_storage.model.User;
import org.example.cloud_storage.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthentificationService authentificationService;

    public UserResponseDto register(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto login(UserRequestDto userRequestDto) {
        authentificationService.authenticate(userRequestDto.username(), userRequestDto.password());
        return userMapper.fromRequestToResponseDto(userRequestDto);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
