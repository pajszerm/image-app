package com.example.imageproject.imageProcessors;

public interface ImageProcessor {
    public byte[] resizeImage(byte[] originalImage, int width, int height);
}
