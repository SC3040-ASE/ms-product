package com.product.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.ProductReadPreviewDTO;
import com.product.dto.product.ProductReadPreviewResponseDTO;
import com.product.dto.product.ProductReadResponseDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Product;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO readProduct(String messageId, Integer id) throws Exception {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            Product foundProduct = product.get();
            List<ImageDTO> images = pictureBlobStorageService.retrieveProductImages(foundProduct.getId());
            ProductReadResponseDTO foundProductDTO = productMapper.mapToProductReadResponse(foundProduct, images);
            return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(foundProductDTO));
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }
    }

    @Transactional
    public ResponseMessageDTO readProductsByOwnerId(String messageId, Integer ownerId, Integer startIndex, Integer endIndex) throws Exception {
        List<Product> products = productRepository.findProductsByOwnerId(ownerId);
        products.sort((a,b)-> b.getCreatedOn().compareTo(a.getCreatedOn()));
        List<Product> subListProduct;
        if(startIndex > products.size()){
            subListProduct = Collections.emptyList();
        }else if(endIndex > products.size()){
            subListProduct = products.subList(startIndex-1, products.size());
        }else{
            subListProduct = products.subList(startIndex-1, endIndex-1);
        }

        List<ProductReadPreviewDTO> productsPreview = productMapper.mapToReadPreviewResults(subListProduct);

        for(ProductReadPreviewDTO productPreview : productsPreview){
            ImageDTO image = pictureBlobStorageService.retrieveOneProductImage(productPreview.getProductId());
            productPreview.setImage(image);
        }

        ProductReadPreviewResponseDTO productReadPreviewResponseDTO = new ProductReadPreviewResponseDTO();
        productReadPreviewResponseDTO.setProducts(productsPreview);
        productReadPreviewResponseDTO.setTotalProducts(products.size());
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(productReadPreviewResponseDTO));
    }


}
