package com.product.service.product;

import com.product.dto.ProductReadResponseDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Product;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;

    @Transactional
    public ResponseMessageDTO readProduct(String messageId, Integer id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            Product foundProduct = product.get();
            String imageFilename = foundProduct.getProductImage();
            String imageBase64 = pictureBlobStorageService.getImage(imageFilename);
            ProductReadResponseDTO foundProductDTO = productMapper.mapToProductReadResponse(foundProduct, imageBase64);
            return new ResponseMessageDTO(messageId, 200, foundProductDTO);
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }
    }
}
