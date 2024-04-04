package com.example.imageproject.controller;

import com.example.imageproject.service.ImageService;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/files")
    public ResponseEntity<String> saveImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("widths") List<Integer> widths,
            @RequestParam("heights") List<Integer> heights
    ) throws NoSuchPaddingException,
            IllegalBlockSizeException,
            IOException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException,
            InterruptedException,
            IM4JavaException {
        imageService.processImages(files, widths, heights);
        return ResponseEntity.ok("Images uploaded successfully.");
    }

    @GetMapping("file/{imageName}")
    public ResponseEntity<FileSystemResource> loadImage(@PathVariable("imageName") String imageName) {
        FileSystemResource imageToLoad = imageService.loadImage(imageName);
        return ResponseEntity.ok(imageToLoad);
    }

}
