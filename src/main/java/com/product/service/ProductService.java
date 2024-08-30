package com.product.service;

import com.product.dto.*;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PictureBlobStorageService pictureBlobStorageService;


    @Transactional
    public ResponseObjectDTO handleUpdateProduct(ProductUpdateDTO productUpdateDTO) {
        Optional<Product> product = productRepository.findById(productUpdateDTO.getId());

        if (product.isPresent()) {
            List<Tag> tags = tagService.saveTagsIfNotExists(productUpdateDTO.getTags());
            Category category = categoryService.saveCategoryIfNotExists(productUpdateDTO.getCategory());

            Product updatedProduct = productMapper.updateProductFromDTO(product.get(), productUpdateDTO, tags, category);
            Product savedProduct = productRepository.saveAndFlush(updatedProduct);

            String imageFilename = "product_" + savedProduct.getId() + ".jpg";
            byte[] imageBytes = Base64.getDecoder().decode(productUpdateDTO.getImageBase64());
            pictureBlobStorageService.saveImage(imageBytes, imageFilename);

            savedProduct.setProductImage(imageFilename);
            productRepository.save(savedProduct);
            return new ResponseObjectDTO("Product updated successfully", updatedProduct);
        } else {
            return new ResponseObjectDTO("Product not found", null);
        }
    }

    @Transactional
    public ResponseObjectDTO handleDeleteProduct(Integer id) {
        productRepository.deleteById(id);
        return new ResponseObjectDTO("Product deleted successfully", null);
    }

    @Transactional
    public ResponseObjectDTO handleSearchProduct(SearchQueryDTO searchQueryDTO) {
        List<Object[]> results = productRepository.searchProducts(searchQueryDTO.getQuery(), searchQueryDTO.getNumberOfResults());
        return new ResponseObjectDTO("Search results", productMapper.mapToSearchResults(results));
    }

    @Transactional
    public ResponseObjectDTO handleCreateProduct(ProductCreationDTO productCreationDTO) {
        try {
            List<Tag> tags = tagService.saveTagsIfNotExists(productCreationDTO.getTags());
            Category category = categoryService.saveCategoryIfNotExists(productCreationDTO.getCategory());

            Product product = productMapper.mapToEntity(productCreationDTO, tags, category);
            Product savedProduct = productRepository.saveAndFlush(product);

            String imageFilename = "product_" + savedProduct.getId() + ".jpg";
            byte[] imageBytes = Base64.getDecoder().decode(productCreationDTO.getImageBase64());
            pictureBlobStorageService.saveImage(imageBytes, imageFilename);

            savedProduct.setProductImage(imageFilename);
            productRepository.save(savedProduct);

            return new ResponseObjectDTO("Product created successfully", savedProduct);
        } catch (Exception e) {
            return new ResponseObjectDTO("Error creating product", null);
        }
    }

    @Transactional
    public ResponseObjectDTO handleReadProduct(Integer id) {
        // TODO: Implement this method
        return null;
    }


}
