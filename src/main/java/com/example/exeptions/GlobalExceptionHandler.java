package com.example.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> FileNotFoundException(Exception e) {
        Map<String, Object> response = createErrorResponse(e,HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Map<String, Object>> FileProcessingException(Exception e) {
        Map<String, Object> response = createErrorResponse(e,HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(FileCreationException.class)
    public ResponseEntity<Map<String, Object>> FileCreationException(Exception e) {
        Map<String, Object> response = createErrorResponse(e,HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(FileInvalidTypeException.class)
    public ResponseEntity<Map<String, Object>> FileInvalidTypeException(Exception e) {
        Map<String, Object> response = createErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response,  HttpStatus.BAD_REQUEST);
    }

    public Map<String, Object> createErrorResponse(Exception massage,HttpStatus status)
    {
        String errorId = status.toString(); // генерируем уникальный идентификатор для ошибки
        Map<String, Object> response = new HashMap<>();

        response.put("errorMessage", "Возникла ошибка: " + massage.getMessage());
        response.put("errorId", errorId);
        return response;
    }


}
