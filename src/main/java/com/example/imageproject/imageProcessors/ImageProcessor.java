package com.example.imageproject.imageProcessors;

import org.im4java.core.IM4JavaException;

import java.io.IOException;

public interface ImageProcessor {

    byte[] resizeImage(byte[] imageData, int width, int height) throws IOException, InterruptedException, IM4JavaException;
}
