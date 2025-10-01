package org.example.cloud_storage.exception;

public class UnavailablePathException extends RuntimeException {
    public UnavailablePathException(String message) {
        super(message);
    }
}
