package com.projects.dreamShops.repository;

import com.projects.dreamShops.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
