package com.example.imageproject.service;

import com.example.imageproject.domain.Image;
import com.example.imageproject.imageProcessors.ImageProcessor;
import com.example.imageproject.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    public void processImages(List<MultipartFile> files, List<String> names, List<Integer> widths, List<Integer> heights) throws IOException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String name = names.get(i);
            int width = widths.get(i);
            int height = heights.get(i);

            byte[] resizedImage = resizeImage(file.getBytes(), width, height);

            byte[] encryptedImage = encryptImage(resizedImage);

            Image imageToSave = createImageData(encryptedImage, name);

            saveImageToDatabase(imageToSave);
        }

    }

    private byte[] resizeImage(byte[] imageData, int width, int height) {
        return imageProcessor.resizeImage(imageData, width, height);
    }

    private byte[] encryptImage(byte[] resizedImage) {
        return null;
    }

    private void saveImageToDatabase(Image image) {
        imageRepository.save(image);
    }

    private Image createImageData(byte[] encryptedImage, String imageName) {
        Image image = new Image();
        image.setData(encryptedImage);
        image.setName(imageName);
        return image;
    }
}
