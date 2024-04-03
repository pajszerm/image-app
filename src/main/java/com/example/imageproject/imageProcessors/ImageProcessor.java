package com.example.imageproject.imageProcessors;

public interface ImageProcessor {

    byte[] resizeImage(byte[] imageData, int width, int height);
}
