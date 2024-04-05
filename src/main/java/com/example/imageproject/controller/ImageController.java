package com.example.imageproject.controller;

import com.example.imageproject.domain.dto.LoadImageDto;
import com.example.imageproject.service.ImageService;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public ResponseEntity<ByteArrayResource> loadImage(@PathVariable("imageName") String imageName) throws
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        LoadImageDto loadImageDto = imageService.loadImageInByteArray(imageName);
        return ResponseEntity.ok()
                .contentType(loadImageDto.getMediaType())
                .body(loadImageDto.getByteArrayResource());
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> loadAllImagesInZip() throws
            NoSuchPaddingException,
            IllegalBlockSizeException,
            IOException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        ByteArrayResource zipResource = imageService.zipAllImages();

        if (zipResource != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "images.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipResource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
