package com.product.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class PictureBlobStorageService {

    @Autowired
    private BlobContainerClient pictureContainerClient;

    public void saveImage(byte[] imageByte, String imgName) {
        BlockBlobClient blobClient = pictureContainerClient
                .getBlobClient(imgName)
                .getBlockBlobClient();

        ByteArrayInputStream dataStream = new ByteArrayInputStream(imageByte);
        blobClient.upload(dataStream, imageByte.length, true);
    }

    public String getImage(String imgName) {
        BlockBlobClient blobClient = pictureContainerClient
                .getBlobClient(imgName)
                .getBlockBlobClient();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.downloadStream(outputStream);

        byte[] imageBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
