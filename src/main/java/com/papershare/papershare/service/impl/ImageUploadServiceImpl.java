package com.papershare.papershare.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.papershare.papershare.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {
    private Storage storageClient;

    @Autowired
    public void setStorageClient(Storage storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public String uploadImage(MultipartFile image) throws IOException {
        Bucket bucket = storageClient.get("paper-share-images");

        String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();

        Blob blob = bucket.create(fileName, image.getBytes());

        return "https://storage.googleapis.com/" + bucket.getName() + "/" + blob.getName();
    }
}
