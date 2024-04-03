package com.example.imageproject.imageProcessors;

import org.springframework.stereotype.Component;

@Component
public class ImageMagickImageProcessor implements ImageProcessor{


    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height) {
        return new byte[0];
    }
}
