package com.product.service;


import com.product.Application;
import com.product.dto.image.ImageDTO;
import com.product.service.blob.PictureBlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class PictureBlobStorageServiceTest {

    @Autowired
    private PictureBlobStorageService pictureBlobService;
    private List<String> base64ImageList;
    private List<String> updateBase64ImageList;

    @BeforeAll
    public void setup() throws Exception {
        base64ImageList = new ArrayList<>();
        updateBase64ImageList = new ArrayList<>();

        File image1 = ResourceUtils.getFile("classpath:image/test-image-1.jpg");
        File image2 = ResourceUtils.getFile("classpath:image/test-image-2.jpg");
        File image3 = ResourceUtils.getFile("classpath:image/test-image-3.jpg");

        byte[] encoded1 = Base64.encodeBase64(FileUtils.readFileToByteArray(image1));
        byte[] encoded2 = Base64.encodeBase64(FileUtils.readFileToByteArray(image2));
        byte[] encoded3 = Base64.encodeBase64(FileUtils.readFileToByteArray(image3));

        base64ImageList.add(new String(encoded1, StandardCharsets.US_ASCII));
        base64ImageList.add(new String(encoded2, StandardCharsets.US_ASCII));
        updateBase64ImageList.add(new String(encoded3, StandardCharsets.US_ASCII));
    }

    @AfterAll
    public void tearDown(){
        pictureBlobService.deleteDirectory(-1);
    }

    @Test
    @DisplayName("Test multiple images")
    void testMultipleImage() {
        pictureBlobService.saveImages(-1, base64ImageList);
        List<ImageDTO> images = pictureBlobService.retrieveProductImages(-1);
        Assertions.assertEquals(images.stream().map(ImageDTO:: getImageBase64).toList(), base64ImageList);
        pictureBlobService.deleteDirectory(-1);
        List<ImageDTO> afterDelete = pictureBlobService.retrieveProductImages(-1);
        Assertions.assertEquals(0, afterDelete.size());
    }

    @Test
    @DisplayName("Test one images")
    void testRetrieveOneImage() {
        pictureBlobService.saveImages(-1, base64ImageList);
        ImageDTO image = pictureBlobService.retrieveOneProductImage(-1);
        Assertions.assertEquals(image.getImageBase64(), base64ImageList.get(0));
        pictureBlobService.deleteDirectory(-1);
        List<ImageDTO> afterDelete = pictureBlobService.retrieveProductImages(-1);
        Assertions.assertEquals(0, afterDelete.size());
    }

    @Test
    @DisplayName("Test update images")
    void testUpdateImages() {
        List<String> imageToBeDeleted = new ArrayList<>();
        imageToBeDeleted.add("product_image_0.jpg");

        pictureBlobService.saveImages(-1, base64ImageList);
        pictureBlobService.updateProductImages(-1,imageToBeDeleted, updateBase64ImageList);

        List<ImageDTO> imagesDTOInBlob = pictureBlobService.retrieveProductImages(-1);
        log.info(String.valueOf(imagesDTOInBlob.size()));
        List<String> imagesInBlob = imagesDTOInBlob.stream().map(ImageDTO::getImageBase64).toList();

        Assertions.assertTrue(imagesInBlob.contains(base64ImageList.get(1)));
        Assertions.assertFalse(imagesInBlob.contains(imageToBeDeleted.get(0)));
        Assertions.assertTrue(imagesInBlob.contains(updateBase64ImageList.get(0)));

        pictureBlobService.deleteDirectory(-1);
    }






}
