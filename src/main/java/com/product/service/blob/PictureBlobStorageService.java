package com.product.service.blob;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.product.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureBlobStorageService {

    private final BlobContainerClient pictureContainerClient;

    public void saveImages(int productId, List<String> imageBase64List) {
        String directory = "product_" + productId;
        for (int i = 0; i < imageBase64List.size(); i++) {
            String imageBase64 = imageBase64List.get(i);
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            BlockBlobClient blobClient = pictureContainerClient
                    .getBlobClient(directory + "/" + "product_image_" + i+".jpg")
                    .getBlockBlobClient();

            ByteArrayInputStream dataStream = new ByteArrayInputStream(imageBytes);
            blobClient.upload(dataStream, imageBytes.length, true);
        }
    }

    public void deleteDirectory(int productId) {
        String directory = "product_" + productId +"/";

        Iterable<BlobItem> blobs = pictureContainerClient.listBlobsByHierarchy(directory);

        for (BlobItem blobItem : blobs) {
            pictureContainerClient.getBlobClient(blobItem.getName()).delete();
        }
    }


    public List<ImageDTO> retrieveProductImages(int productId) {
        String directory = "product_" + productId + "/";

        List<ImageDTO> images = new ArrayList<>();

        pictureContainerClient.listBlobsByHierarchy(directory).forEach(blobItem -> {
            BlobClient blobClient = pictureContainerClient.getBlobClient(blobItem.getName());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            ImageDTO newImage = new ImageDTO();
            newImage.setImageBase64(Base64.getEncoder().encodeToString(imageBytes));
            newImage.setImageName(blobItem.getName());
            images.add(newImage);
        });

        return images;
    }

    public void updateProductImages(int productId, List<String> imagesToDelete, List<String> base64ImagesToAdd) {
        String directory = "product_" + productId + "/";

        for (String imageName : imagesToDelete) {
            String filePath = directory + imageName;
            pictureContainerClient.getBlobClient(filePath).delete();
        }

        List<String> existingImages = pictureContainerClient.listBlobsByHierarchy(directory)
                .stream()
                .map(BlobItem::getName)
                .toList();

        int nextIndex = existingImages.stream()
                .mapToInt(name -> {
                    String[] parts = name.split("[_.]");
                    if (parts.length > 2) {
                        try {
                            return Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max()
                .orElse(-1) + 1;

        for (String base64Image : base64ImagesToAdd) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String imageName = "product_image_" + nextIndex + ".jpg";
            BlockBlobClient blobClient = pictureContainerClient
                    .getBlobClient(directory + imageName)
                    .getBlockBlobClient();

            ByteArrayInputStream dataStream = new ByteArrayInputStream(imageBytes);
            blobClient.upload(dataStream, imageBytes.length, true);

            nextIndex++;
        }
    }
}
