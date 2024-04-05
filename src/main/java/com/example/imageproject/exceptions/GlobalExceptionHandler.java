package com.example.imageproject.exceptions;

import jakarta.persistence.EntityNotFoundException;
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

    @ExceptionHandler(ImageNameAlreadyExistsException.class)
    public ResponseEntity<String> handleImageNameAlreadyExistsException(ImageNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleImageNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to load image: " + ex.getMessage());
    }

    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<String> handleMissingDataException(MissingDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload images: " + ex.getMessage());
    }

}
