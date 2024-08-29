package com.product.service;

import com.product.dto.ProductReceived;
import com.product.dto.ProductSearchResult;
import com.product.dto.ResponseObject;
import com.product.dto.SearchQuery;
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


    public ResponseObject handleUpdateProduct(ProductReceived productDetails) {
        //TODO: Implement this method
        return null;
    }

    public ResponseObject handleDeleteProduct(Long id) {
        productRepository.deleteById(id);
        return new ResponseObject("Product deleted successfully", null);
    }

    public ResponseObject handleSearchProduct(SearchQuery searchQuery) {
        List<Object[]> results = productRepository.searchProducts(searchQuery.getQuery(), searchQuery.getNumberOfResults());
        return new ResponseObject("Search results", productMapper.mapToSearchResults(results));
    }

    public ResponseObject handleCreateProduct(ProductReceived productReceived) {
        try {
            List<String> tagNames = productReceived.getTags();
            List<Tag> existingTags = tagRepository.findTagsByTagNames(tagNames);


            Set<String> existingTagNames = existingTags.stream().map(Tag::getTagName).collect(Collectors.toSet());
            List<Tag> newTags = tagNames.stream()
                    .filter(tagName -> !existingTagNames.contains(tagName))
                    .map(Tag::new)
                    .collect(Collectors.toList());

            List<Tag> savedNewTags = tagRepository.saveAll(newTags);
            existingTags.addAll(savedNewTags);

            Category category = categoryRepository.findByName(productReceived.getCategory())
                    .orElseGet(() -> categoryRepository.save(new Category(productReceived.getCategory())));

            Product product = productMapper.mapToEntity(productReceived, existingTags, category);

            Product savedProduct = productRepository.saveAndFlush(product);

            String imageFilename = "product_" + savedProduct.getId() + ".jpg";
            byte[] imageBytes = Base64.getDecoder().decode(productReceived.getImageBase64());
            pictureBlobStorageService.saveImage(imageBytes, imageFilename);

            savedProduct.setProductImage(imageFilename);
            productRepository.save(savedProduct);
            return new ResponseObject("Product created successfully", savedProduct);
        } catch (Exception e) {
            return new ResponseObject("Error creating product", null);
        }
    }
}
