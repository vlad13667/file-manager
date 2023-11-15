package com.example.exeptions;

public class FileInvalidTypeException extends RuntimeException {
    public FileInvalidTypeException(String message) {
        super(message);
    }
}