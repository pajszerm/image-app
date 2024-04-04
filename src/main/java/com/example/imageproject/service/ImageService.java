package com.example.imageproject.service;

import com.example.imageproject.domain.Image;
import com.example.imageproject.exceptions.ImageDimensionValidationException;
import com.example.imageproject.exceptions.ImageFormatValidationException;
import com.example.imageproject.imageProcessors.ImageProcessor;
import com.example.imageproject.repository.ImageRepository;
import com.example.imageproject.utils.SecretKeyManager;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    private final ImageProcessor imageProcessor;

    private final SecretKeyManager secretKeyManager;

    @Value("${spring.image.max.width}")
    private int maxWidth;

    @Value("${spring.image.max.height}")
    private int maxHeight;

    List<String> acceptableFormats = Arrays.asList("jpg", "jpeg", "png");

    @Autowired
    public ImageService(ImageRepository imageRepository, ImageProcessor imageProcessor, SecretKeyManager secretKeyManager) {
        this.imageRepository = imageRepository;
        this.imageProcessor = imageProcessor;
        this.secretKeyManager = secretKeyManager;
    }

    public void processImages(List<MultipartFile> files, List<Integer> widths, List<Integer> heights) throws
            IOException,
            InterruptedException,
            IM4JavaException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String name = files.get(i).getOriginalFilename();
            int width = widths.get(i);
            int height = heights.get(i);
            String format = extractFormat(name);

            validateFileFormat(format, name);
            validateDimensions(width, height, name);

            byte[] resizedImage = resizeImage(file.getBytes(), width, height, format);
            byte[] encryptedImage = encryptImage(resizedImage);

            Image imageToSave = createImageData(encryptedImage, name);
            saveImageToDatabase(imageToSave);
        }
    }

    private void validateFileFormat(String format, String name) {
        if (!acceptableFormats.contains(format.toLowerCase())) {
            throw new ImageFormatValidationException("Invalid image format: " + name +
                    ". Only JPEG, JPG, and PNG are accepted.");
        }
    }

    private void validateDimensions(int width, int height, String name) {
        if (width > maxWidth || height > maxHeight) {
            throw new ImageDimensionValidationException("Invalid image dimensions: " + name +
                    ". Maximum allowed dimensions are " + maxWidth + "x" + maxHeight + ".");
        }
    }

    private String extractFormat(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            throw new IllegalArgumentException("Invalid file: " + name +
                    ". Only JPEG, JPG and PNG images are accepted.");
        }
        return name.substring(dotIndex + 1);
    }

    private byte[] resizeImage(byte[] imageData, int width, int height, String format) throws
            IOException,
            InterruptedException,
            IM4JavaException {
        return imageProcessor.resizeImage(imageData, width, height, format);
    }

    private byte[] encryptImage(byte[] resizedImage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = secretKeyManager.getSecretKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedImageData = cipher.doFinal(resizedImage);
        return encryptedImageData;
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
