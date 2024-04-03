package com.example.imageproject.repository;

import com.example.imageproject.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
