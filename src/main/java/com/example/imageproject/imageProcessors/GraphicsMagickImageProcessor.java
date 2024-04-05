package com.example.imageproject.imageProcessors;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Primary
public class GraphicsMagickImageProcessor implements ImageProcessor {

    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height, String format) throws
            IOException,
            InterruptedException,
            IM4JavaException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ConvertCmd cmd = new ConvertCmd(true);

        IMOperation op = new IMOperation();
        op.addImage("-");
        op.resize(width, height);
        op.addImage( format + ":-");

        cmd.setInputProvider(new org.im4java.process.Pipe(inputStream, null));
        cmd.setOutputConsumer(new org.im4java.process.Pipe(null, outputStream));
        cmd.run(op);

        return outputStream.toByteArray();
    }
}

