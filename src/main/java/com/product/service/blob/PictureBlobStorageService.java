package com.product.service.blob;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PictureBlobStorageService {

    private final BlobContainerClient pictureContainerClient;

    public void saveImage(byte[] imageByte, String imgName) {
        BlockBlobClient blobClient = pictureContainerClient
                .getBlobClient(imgName)
                .getBlockBlobClient();

        ByteArrayInputStream dataStream = new ByteArrayInputStream(imageByte);
        blobClient.upload(dataStream, imageByte.length, true);
    }

    public void deleteImage(String imgName) {
        BlockBlobClient blobClient = pictureContainerClient
                .getBlobClient(imgName)
                .getBlockBlobClient();

        blobClient.delete();
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
