package com.product.service.product;

import com.product.dto.ProductDeleteRequestDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Product;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductDeleteService {

    private final ProductRepository productRepository;
    private final PictureBlobStorageService pictureBlobStorageService;

    @Transactional
    public ResponseMessageDTO deleteProduct(String messageId, ProductDeleteRequestDTO targetProduct) {
        Integer productId = targetProduct.getProductId();
        Integer ownerId = targetProduct.getOwnerId();
        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {
            if (!product.get().getOwnerId().equals(ownerId)) {
                return new ResponseMessageDTO(messageId, 403, "Unauthorized");
            }
            productRepository.deleteById(productId);
            pictureBlobStorageService.deleteImage(product.get().getProductImage());
            return new ResponseMessageDTO(messageId, 200, "Product deleted successfully");
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }
    }
}
