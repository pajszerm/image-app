package com.example.imageproject.controller;

import com.example.imageproject.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/files")
    public ResponseEntity<String> saveImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("width") List<Integer> widths,
            @RequestParam("height") List<Integer> heights,
            @RequestParam("names") List<String> names
    ) {
        try {
            imageService.processImages(files, names, widths, heights);
            return ResponseEntity.ok("Images uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images: " + e.getMessage());
        }
    }
}
