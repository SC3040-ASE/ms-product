package com.product.service.product;

import com.product.dto.ImageDTO;
import com.product.dto.ProductReadResponseDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Product;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
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
            List<ImageDTO> images = pictureBlobStorageService.retrieveProductImages(foundProduct.getId());
            ProductReadResponseDTO foundProductDTO = productMapper.mapToProductReadResponse(foundProduct, images);
            return new ResponseMessageDTO(messageId, 200, foundProductDTO);
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }
    }
}
