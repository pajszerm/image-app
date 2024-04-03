package com.example.imageproject.imageProcessors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@Primary
public class GraphicsMagickImageProcessor implements ImageProcessor {

    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height) {
        return new byte[0];
    }
}
