package com.example.imageproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadImageDto {

    private ByteArrayResource byteArrayResource;

    private MediaType mediaType;
}
