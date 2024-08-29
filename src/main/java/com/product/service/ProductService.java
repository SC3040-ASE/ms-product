package com.product.service;

import com.product.dto.ProductCreationDTO;
import com.product.dto.ResponseObjectDTO;
import com.product.dto.SearchQueryDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.mapper.ProductMapper;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PictureBlobStorageService pictureBlobStorageService;


    public ResponseObjectDTO handleUpdateProduct(ProductCreationDTO productDetails) {
        //TODO: Implement this method
        return null;
    }

    public ResponseObjectDTO handleDeleteProduct(Long id) {
        productRepository.deleteById(id);
        return new ResponseObjectDTO("Product deleted successfully", null);
    }

    public ResponseObjectDTO handleSearchProduct(SearchQueryDTO searchQueryDTO) {
        List<Object[]> results = productRepository.searchProducts(searchQueryDTO.getQuery(), searchQueryDTO.getNumberOfResults());
        return new ResponseObjectDTO("Search results", productMapper.mapToSearchResults(results));
    }

    public ResponseObjectDTO handleCreateProduct(ProductCreationDTO productCreationDTO) {
        try {
            List<String> tagNames = productCreationDTO.getTags();
            List<Tag> existingTags = tagRepository.findTagsByTagNames(tagNames);


            Set<String> existingTagNames = existingTags.stream().map(Tag::getTagName).collect(Collectors.toSet());
            List<Tag> newTags = tagNames.stream()
                    .filter(tagName -> !existingTagNames.contains(tagName))
                    .map(Tag::new)
                    .collect(Collectors.toList());

            List<Tag> savedNewTags = tagRepository.saveAll(newTags);
            existingTags.addAll(savedNewTags);

            Category category = categoryRepository.findByName(productCreationDTO.getCategory())
                    .orElseGet(() -> categoryRepository.save(new Category(productCreationDTO.getCategory())));

            Product product = productMapper.mapToEntity(productCreationDTO, existingTags, category);

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
}
