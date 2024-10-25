package com.product.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.product.ProductOrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class ProductOrderTriggerService {

    @Value("${ms-order.url}")
    private String orderBaseUrl;

    private final ObjectMapper objectMapper;

    public void triggerOrderRequest(ProductOrderRequestDTO productOrderRequestDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String orderUrl = orderBaseUrl + "/order-requests/products";
        restTemplate.postForObject(orderUrl, productOrderRequestDTO, String.class);

        String requestJson = objectMapper.writeValueAsString(productOrderRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);

        restTemplate.postForEntity(orderUrl, entity, String.class);


    }
}
