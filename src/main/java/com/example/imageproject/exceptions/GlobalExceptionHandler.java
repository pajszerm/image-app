package com.example.imageproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ImageDimensionValidationException.class)
    public ResponseEntity<String> handleImageDimensionValidationException(ImageDimensionValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload images: " + ex.getMessage());
    }

    @ExceptionHandler(ImageFormatValidationException.class)
    public ResponseEntity<String> handleImageFormatValidationException(ImageFormatValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload images: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images: " + ex.getMessage());
    }

}
