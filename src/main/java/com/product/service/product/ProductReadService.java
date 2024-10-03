package com.product.service.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.*;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.user.UsersIdRequestDTO;
import com.product.dto.user.UsersTelegramHandleDTO;
import com.product.entity.Product;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;
    private final ObjectMapper objectMapper;

    @Value("${ms-order.url}")
    private String orderBaseUrl;

    @Value("${ms-user.url}")
    private String userBaseUrl;


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

    @Transactional
    public ResponseMessageDTO readProductsReserved(String messageId, Integer ownerId) throws Exception{
        RestTemplate restTemplate = new RestTemplate();

        String orderUrl = orderBaseUrl + "/orders/products";

        String orderParamUrl = UriComponentsBuilder.fromHttpUrl(orderUrl)
                .queryParam("userId", ownerId)
                .encode()
                .toUriString();

        ResponseEntity<String> orderResponseEntity = restTemplate.exchange(orderParamUrl, HttpMethod.GET, null, String.class);
        List<ProductOrderDTO> productOrderDTOS = objectMapper.readValue(orderResponseEntity.getBody(),
                new TypeReference<>() {
                });

        if(productOrderDTOS == null){
            return new ResponseMessageDTO(messageId,200, objectMapper.writeValueAsString(new ArrayList<>()));
        }

        // get product's detail
        List<Integer> productIds = productOrderDTOS.stream().map(ProductOrderDTO::getProductId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        // get telegram id
        List<Integer> buyersId = productOrderDTOS.stream().map(ProductOrderDTO::getBuyerId).toList();
        String requestJson = objectMapper.writeValueAsString(new UsersIdRequestDTO(buyersId));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);

        String userUrl = userBaseUrl + "/api/v1/user/getTelehandleById";
        ResponseEntity<String> userResponseEntity = restTemplate.postForEntity(userUrl, entity, String.class);
        UsersTelegramHandleDTO usersTelegram = objectMapper.readValue(userResponseEntity.getBody(), UsersTelegramHandleDTO.class);

        List<ProductReservedDTO> productsReserved = productMapper.mapToProductsReserved(products, productOrderDTOS, usersTelegram.getTelehandleResponseList());

        for(ProductReservedDTO productReserved: productsReserved){
            productReserved.setImage(pictureBlobStorageService.retrieveOneProductImage(productReserved.getProductId()));
        }



        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(productsReserved));
    }


}
