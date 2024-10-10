package com.product.service.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.*;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.user.TelehandleResponse;
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
        RestTemplate restTemplate = new RestTemplate();


        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            Product foundProduct = product.get();
            List<ImageDTO> images = pictureBlobStorageService.retrieveProductImages(foundProduct.getId());
            String userUrl = userBaseUrl + "/api/v1/user/getUsernameById?userId=" + foundProduct.getOwnerId();

            ResponseEntity<String> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, null, String.class);
            String ownerUsername = userResponse.getBody();

            if(ownerUsername == null){
                return new ResponseMessageDTO(messageId, 500, "Failed to get owner username");
            }

            ProductReadResponseDTO foundProductDTO = productMapper.mapToProductReadResponse(foundProduct, images, ownerUsername);
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
    public ResponseMessageDTO readProductsReserved(String messageId, Integer ownerId, Boolean isBuyer, String orderStatus) throws Exception{
        RestTemplate restTemplate = new RestTemplate();

        String orderUrl = orderBaseUrl + "/orders/products";

        String orderParamUrl = UriComponentsBuilder.fromHttpUrl(orderUrl)
                .queryParam("userId", ownerId)
                .queryParam("isBuyer", isBuyer)
                .queryParam("status", orderStatus)
                .encode()
                .toUriString();

        ResponseEntity<String> orderResponseEntity = restTemplate.exchange(orderParamUrl, HttpMethod.GET, null, String.class);
        List<ProductOrderDTO> productOrderDTOS = objectMapper.readValue(orderResponseEntity.getBody(),
                new TypeReference<>() {
                });

        if(productOrderDTOS == null){
            return new ResponseMessageDTO(messageId,200, objectMapper.writeValueAsString(new ArrayList<>()));
        }

        if(productOrderDTOS.isEmpty()){
            return new ResponseMessageDTO(messageId,200, objectMapper.writeValueAsString(new ArrayList<>()));
        }

        // get product's detail
        List<Integer> productIds = productOrderDTOS.stream().map(ProductOrderDTO::getProductId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        // get telegram id
        Set<Integer> usersId = productOrderDTOS.stream().map(ProductOrderDTO::getBuyerId).collect(HashSet::new, HashSet::add, HashSet::addAll);
        usersId.add(ownerId);
        String requestJson = objectMapper.writeValueAsString(new UsersIdRequestDTO(new ArrayList<>(usersId)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);

        String userUrl = userBaseUrl + "/api/v1/user/getTelehandleById";
        ResponseEntity<String> userResponseEntity = restTemplate.postForEntity(userUrl, entity, String.class);
        UsersTelegramHandleDTO usersTelegram = objectMapper.readValue(userResponseEntity.getBody(), UsersTelegramHandleDTO.class);

        List<TelehandleResponse> telehandleResponseList = usersTelegram.getTelehandleResponseList();
        Map<Integer, String> userTelegramMap = new HashMap<>();
      
        for(int i=0;i<telehandleResponseList.size();i++){
            userTelegramMap.put(telehandleResponseList.get(i).getUserId(), telehandleResponseList.get(i).getTelegram_handle());
        }
        List<ProductReservedDTO> productsReserved = productMapper.mapToProductsReserved(products, productOrderDTOS, userTelegramMap, ownerId);

        for(ProductReservedDTO productReserved: productsReserved){
            productReserved.setImage(pictureBlobStorageService.retrieveOneProductImage(productReserved.getProductId()));
        }



        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(productsReserved));
    }


}
