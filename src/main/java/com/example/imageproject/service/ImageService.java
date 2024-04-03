package com.example.imageproject.service;

import com.example.imageproject.imageProcessors.ImageProcessor;
import com.example.imageproject.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    private final ImageProcessor imageProcessor;

    @Autowired
    public ImageService(ImageRepository imageRepository, ImageProcessor imageProcessor) {
        this.imageRepository = imageRepository;
        this.imageProcessor = imageProcessor;
    }


}
