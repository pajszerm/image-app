package com.example.imageproject.imageProcessors;

import org.springframework.stereotype.Component;

@Component
public class ImageMagickImageProcessor implements ImageProcessor {


    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height, String format) {
        //TODO: implement imageMagick image resizing
        return new byte[0];
    }
}

