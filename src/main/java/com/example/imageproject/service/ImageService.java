package com.example.imageproject.service;

import com.example.imageproject.domain.Image;
import com.example.imageproject.domain.dto.LoadImageDto;
import com.example.imageproject.exceptions.ImageDimensionValidationException;
import com.example.imageproject.exceptions.ImageFormatValidationException;
import com.example.imageproject.exceptions.ImageNameAlreadyExistsException;
import com.example.imageproject.imageProcessors.ImageProcessor;
import com.example.imageproject.repository.ImageRepository;
import com.example.imageproject.utils.SecretKeyManager;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    private static final List<String> ACCEPTABLE_FORMATS = Arrays.asList("jpg", "jpeg", "png");

    private static final String ALGORITHM = "AES";

    private static final Map<String, MediaType> FORMAT_MEDIA_TYPE_MAP = new HashMap<>();

    static {
        FORMAT_MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        FORMAT_MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        FORMAT_MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
    }

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
            int width = (widths != null && widths.size() > i) ? widths.get(i) : 5000;
            int height = (heights != null && heights.size() > i) ? heights.get(i) : 5000;
            String format = extractFormat(name);

            validateImageName(name);
            validateFileFormat(format, name);
            validateDimensions(width, height, name);

            byte[] resizedImage = resizeImage(file.getBytes(), width, height, format);
            byte[] encryptedImage = encryptImage(resizedImage);

            Image imageToSave = createImageData(encryptedImage, name);
            saveImageToDatabase(imageToSave);
        }
    }

    private void validateImageName(String imageName) {
        if (imageRepository.existsByName(imageName)) {
            throw new ImageNameAlreadyExistsException("The image name: " + imageName +
                    " is already exists, please choose another one!");
        }
    }

    private void validateFileFormat(String format, String name) {
        if (!ACCEPTABLE_FORMATS.contains(format.toLowerCase())) {
            throw new ImageFormatValidationException("Invalid image format: " + name +
                    ". Only JPEG, JPG, and PNG are accepted.");
        }
    }

    private void validateDimensions(int width, int height, String name) {
        if ((width == 5000 && height != 5000) || (width != 5000 && height == 5000)) {
            throw new IllegalArgumentException("Both width and height must be provided or both set to default.");
        }

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

    private byte[] encryptImage(byte[] resizedImage) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        SecretKey secretKey = secretKeyManager.getSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedImageData = cipher.doFinal(resizedImage);
        return encryptedImageData;
    }

    private byte[] decryptImage(byte[] imageToDecrypt) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        SecretKey secretKey = secretKeyManager.getSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedImageData = cipher.doFinal(imageToDecrypt);
        return decryptedImageData;
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


    public LoadImageDto loadImageInByteArray(String imageName) throws
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        Image imageToLoad = imageRepository.findByName(imageName);
        byte[] decryptedImageData = decryptImage(imageToLoad.getData());
        ByteArrayResource byteArrayResource = new ByteArrayResource(decryptedImageData);
        MediaType mediaType = setMediaType(imageName);
        return new LoadImageDto(byteArrayResource, mediaType);
    }

    private MediaType setMediaType(String imageName) {
        String format = extractFormat(imageName);
        return FORMAT_MEDIA_TYPE_MAP.getOrDefault(format.toLowerCase(), MediaType.IMAGE_JPEG);
    }

    public ByteArrayResource zipAllImages() throws
            IOException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        List<Image> images = imageRepository.findAll();

        if (images.isEmpty()) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        for (Image image : images) {
            ZipEntry zipEntry = new ZipEntry(image.getName());
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(decryptImage(image.getData()));
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        }

        ByteArrayResource zipResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return zipResource;
    }
}
