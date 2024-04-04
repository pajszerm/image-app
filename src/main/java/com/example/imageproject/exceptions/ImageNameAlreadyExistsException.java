package com.example.imageproject.exceptions;

public class ImageNameAlreadyExistsException extends RuntimeException{
    public ImageNameAlreadyExistsException(String message) {
        super(message);
    }
}
