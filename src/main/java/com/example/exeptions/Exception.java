package com.example.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Exception extends ResponseStatusException {
    public Exception(HttpStatus status, String reason) {
        super(status, reason);
    }


}
