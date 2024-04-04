package com.example.imageproject.imageProcessors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class ImageMagickImageProcessor implements ImageProcessor {


    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height, String format) {
        return new byte[0];
    }
}

