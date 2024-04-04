package com.example.imageproject.imageProcessors;

import com.example.imageproject.repository.ImageRepository;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Key;

public class Test {

    public static void main(String[] args) throws Exception {

        byte[] imageData = Files.readAllBytes(Path.of("C:\\Users\\andra\\IdeaProjects\\image-project\\src\\main\\resources\\test_image.jpg"));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ConvertCmd cmd = new ConvertCmd(true);


        IMOperation op = new IMOperation();
        op.addImage("-");
        op.resize(300, 300);
        op.addImage("jpg:-");


        cmd.setInputProvider(new org.im4java.process.Pipe(inputStream, null));
        cmd.setOutputConsumer(new org.im4java.process.Pipe(null, outputStream));
        cmd.run(op);

        byte[] resizedImageDAta = outputStream.toByteArray();

        String keyString = "ThisIsASecretKey";
        byte[] encryptedImageData = encryptData(resizedImageDAta, keyString);

        Files.write(Path.of("C:\\Users\\andra\\IdeaProjects\\image-project\\src\\main\\resources\\enc.jpg"), encryptedImageData);

        byte[] toDecrypt = Files.readAllBytes(Path.of("C:\\Users\\andra\\IdeaProjects\\image-project\\src\\main\\resources\\enc.jpg"));

        byte[] decryptedImgData = decryptData(toDecrypt, keyString);

        Files.write(Path.of("C:\\Users\\andra\\IdeaProjects\\image-project\\src\\main\\resources\\readable.jpg"), decryptedImgData);

    }

    public static byte[] encryptData(byte[] data, String keyString) throws Exception {
        byte[] keyBytes = keyString.getBytes();
        Key key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decryptData(byte[] encryptedData, String keyString) throws Exception {
        byte[] keyBytes = keyString.getBytes();
        Key key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

}
