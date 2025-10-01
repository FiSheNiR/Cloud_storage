package org.example.cloud_storage.exception;

public class UnautorizedException extends RuntimeException {
    public UnautorizedException(String message) {
        super(message);
    }
}
