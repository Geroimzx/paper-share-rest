package com.papershare.papershare.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploadService {

    String uploadImage(MultipartFile image) throws IOException;
}
