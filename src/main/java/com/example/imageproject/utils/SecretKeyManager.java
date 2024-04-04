package com.example.imageproject.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Component
public class SecretKeyManager {

    @Value("${spring.key.file.path}")
    private String keyFilePath;
    private SecretKey secretKey;
    private final SecretKeyGenerator keyGenerator;

    @Autowired
    public SecretKeyManager(SecretKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @PostConstruct
    public void initializeKey() {
        try {
            if (Files.exists(Paths.get(keyFilePath))) {
                secretKey = keyGenerator.loadKeyFromFile(keyFilePath);
            } else {
                secretKey = keyGenerator.generateKey();
                keyGenerator.saveKeyToFile(secretKey, keyFilePath);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

}

