package com.example.exeptions;

public class FileCreationException extends RuntimeException {
    public FileCreationException(String message) {
        super(message);
    }
}

