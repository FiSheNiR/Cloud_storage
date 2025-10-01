package org.example.cloud_storage.advice;

import lombok.extern.slf4j.Slf4j;
import org.example.cloud_storage.dto.ErrorResponseDto;
import org.example.cloud_storage.exception.UnautorizedException;
import org.example.cloud_storage.exception.UserAlreadyExistsException;
import org.example.cloud_storage.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(ValidationException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto userAlreadyExistsException(UserAlreadyExistsException e) {
        return new ErrorResponseDto("User already exists");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto badCredentialsExceptionHandler() {
        return new ErrorResponseDto("Bad credentials");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto usernameNotFoundException(UsernameNotFoundException e) {
        return new ErrorResponseDto("User not found or incorrect password");
    }

    @ExceptionHandler(UnautorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto unAuthorizedExceptionHandler(UsernameNotFoundException e) {
        return new ErrorResponseDto(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponseDto("Internal Server Error in Exception Handler");
    }
}
