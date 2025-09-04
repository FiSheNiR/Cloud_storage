package org.example.cloud_storage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.dto.UserResponseDto;
import org.example.cloud_storage.mapper.UserMapper;
import org.example.cloud_storage.model.User;
import org.example.cloud_storage.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextHolderStrategy securityContext;
    private final AuthenticationService authenticationService;

    public UserResponseDto register(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto login(UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationService.authenticate(userRequestDto.username(), userRequestDto.password(), request, response);
        return new UserResponseDto(authentication.getName());
    }

    public void logout() {
        securityContext.clearContext();
    }
}
