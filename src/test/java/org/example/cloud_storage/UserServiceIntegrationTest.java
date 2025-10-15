package org.example.cloud_storage;

import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.exception.UserAlreadyExistsException;
import org.example.cloud_storage.model.User;
import org.example.cloud_storage.repository.UserRepository;
import org.example.cloud_storage.service.MinioService;
import org.example.cloud_storage.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Testcontainers
public class UserServiceIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_shouldPersistUserInDatabase() {
        UserRequestDto userRequestDto = new UserRequestDto("123456", "123456");
        userService.register(userRequestDto);
        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers.size()).isEqualTo(1);
        assertThat(allUsers.get(0).getUsername()).isEqualTo(userRequestDto.username());
        minioService.deleteBucket(userRequestDto.username());
    }

    @Test
    public void createUser_withDuplicateUsername_shouldThrowUsernameAlreadyExistsException() {
        UserRequestDto userRequestDto = new UserRequestDto("123456", "12345");
        userService.register(userRequestDto);
        minioService.deleteBucket(userRequestDto.username());
        assertThatThrownBy(() -> userService.register(userRequestDto))
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}
