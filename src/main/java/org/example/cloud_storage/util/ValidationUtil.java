package org.example.cloud_storage.util;

import org.example.cloud_storage.dto.UserRequestDto;
import org.example.cloud_storage.exception.ValidationException;

public class ValidationUtil {

    public static void isValidUserCredentials(UserRequestDto userRequestDto) {
        validUsername(userRequestDto.username());
        validPassword(userRequestDto.password());
    }

    private static void validUsername(String uncheckedUsername) {
        String username = uncheckedUsername.trim();
        if (username.length() < 5 || username.length() > 50) {
            throw new ValidationException("Username must be between 5 and 50 characters");
        }
    }

    private static void validPassword(String uncheckedPassword) {
        String password = uncheckedPassword.trim();
        if (password.length() < 5 ||  password.length() > 50) {
            throw new ValidationException("Password must be between 5 and 50 characters");
        }
    }
}
