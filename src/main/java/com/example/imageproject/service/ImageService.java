package com.example.imageproject.service;

import com.example.imageproject.domain.Image;
import com.example.imageproject.imageProcessors.ImageProcessor;
import com.example.imageproject.repository.ImageRepository;
import com.example.imageproject.utils.SecretKeyManager;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    private final ImageProcessor imageProcessor;

    private final SecretKeyManager secretKeyManager;

    @Autowired
    public ImageService(ImageRepository imageRepository, ImageProcessor imageProcessor, SecretKeyManager secretKeyManager) {
        this.imageRepository = imageRepository;
        this.imageProcessor = imageProcessor;
        this.secretKeyManager = secretKeyManager;
    }

    public void processImages(List<MultipartFile> files, List<Integer> widths, List<Integer> heights) throws IOException, InterruptedException, IM4JavaException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String name = files.get(i).getOriginalFilename();
            int width = widths.get(i);
            int height = heights.get(i);

            byte[] resizedImage = resizeImage(file.getBytes(), width, height);

            byte[] encryptedImage = encryptImage(resizedImage);

            Image imageToSave = createImageData(encryptedImage, name);

            saveImageToDatabase(imageToSave);
        }

    }

    private byte[] resizeImage(byte[] imageData, int width, int height) throws IOException, InterruptedException, IM4JavaException {
        return imageProcessor.resizeImage(imageData, width, height);
    }

    private byte[] encryptImage(byte[] resizedImage) {
        try {
            SecretKey secretKey = secretKeyManager.getSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedImageData = cipher.doFinal(resizedImage);
            return encryptedImageData;
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException |
                 BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
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
