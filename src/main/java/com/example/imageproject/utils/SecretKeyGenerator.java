package com.example.imageproject.utils;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Component
public class SecretKeyGenerator {
    private static final int KEY_SIZE = 256;

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        return generator.generateKey();
    }

    public SecretKey loadKeyFromFile(String filePath) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
        return new SecretKeySpec(keyBytes, "AES");
    }

    public void saveKeyToFile(SecretKey key, String filePath) throws IOException {
        byte[] keyBytes = key.getEncoded();
        Files.write(Paths.get(filePath), keyBytes);
    }
}
