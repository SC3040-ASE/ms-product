package com.product.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.product.dto.*;
import com.product.dto.category.*;
import com.product.service.category.CategoryService;
import com.product.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InboundConfiguration {
    private final PubSubConfiguration pubSubConfiguration;
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OutboundConfiguration.PubsubOutboundGateway messagingGateway;

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
        @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
        PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, pubSubConfiguration.getSubscription());
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }

    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            String payload = new String((byte[]) message.getPayload());
            String responseId = null;
            try {
                RequestMessageDTO requestMessage = objectMapper.readValue(payload, RequestMessageDTO.class);
                log.info("request message: {}", requestMessage);
                responseId = requestMessage.getId();
                ResponseMessageDTO responseMessage = newHandler(requestMessage);

                String responsePayload = objectMapper.writeValueAsString(responseMessage);
                // Do we need different topics for product / category / tag?
                messagingGateway.sendToPubSub(responsePayload);

            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                try{
                    ResponseMessageDTO responseMessage = new ResponseMessageDTO(responseId, 500, "Internal server error");
                    String responsePayload = objectMapper.writeValueAsString(responseMessage);
                    messagingGateway.sendToPubSub(responsePayload);
                } catch (Exception ex) {
                    log.error("Error: {}", ex.getMessage());
                }
            } finally {
                BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
                originalMessage.ack();
            }
        };
    }

    public ResponseMessageDTO oldHandler(RequestMessageDTO requestMessage) throws JsonProcessingException {
        log.info("Received message: {}", requestMessage);
        String messageMethod = requestMessage.getMethod();
        String messagePath = requestMessage.getPath();
        String requestBody = requestMessage.getBody();
        String responseId = requestMessage.getId();

        ResponseMessageDTO responseMessage;
        switch (messageMethod) {
            case "GET":
                switch (messagePath) {
                    case "/product":
                        Integer readProductId = objectMapper.readValue(requestBody, Integer.class);
                        log.info("Received read request for product with id: {}", readProductId);
                        responseMessage = productService.handleReadProduct(responseId, readProductId);
                        break;
                    case "/product/search":
                        ProductSearchRequestDTO productSearchRequestDTO = objectMapper.readValue(requestBody, ProductSearchRequestDTO.class);
                        log.info("Received search request: {}", requestBody);
                        responseMessage = productService.handleSearchProduct(responseId, productSearchRequestDTO);
                        break;
                    default:
                        log.warn("Unknown message path: {}", messagePath);
                        responseMessage = new ResponseMessageDTO(responseId, 404, null);
                        break;
                }
                break;
            case "POST":
                ProductCreationRequestDTO productCreationRequestDTO = objectMapper.readValue(requestBody, ProductCreationRequestDTO.class);
                log.info("Received product: {}", productCreationRequestDTO);
                responseMessage = productService.handleCreateProduct(responseId, productCreationRequestDTO);
                break;
            case "PUT":
                ProductUpdateRequestDTO productUpdate = objectMapper.readValue(requestBody, ProductUpdateRequestDTO.class);
                log.info("Received product update: {}", productUpdate);
                responseMessage = productService.handleUpdateProduct(responseId, productUpdate);
                break;
            case "DELETE":
                ProductDeleteRequestDTO productDeleteRequestDTO = objectMapper.readValue(requestBody, ProductDeleteRequestDTO.class);
                log.info("Received delete request for product with id: {}", productDeleteRequestDTO);
                responseMessage = productService.handleDeleteProduct(responseId, productDeleteRequestDTO);
                break;
            default:
                log.warn("Unknown message type: {}", messageMethod);
                responseMessage = new ResponseMessageDTO(responseId, 404, null);
                break;
        }
        return responseMessage;
    }

    public ResponseMessageDTO newHandler(RequestMessageDTO requestMessage) throws JsonProcessingException {
        String messagePath = requestMessage.getPath();
        ResponseMessageDTO responseMessageDTO;
        if (messagePath.contains("product")) {
            System.out.println("Going to product.");
            responseMessageDTO = handleProductRequests(requestMessage);
        } else if (messagePath.contains("category")) {
            System.out.println("Going to category.");
            responseMessageDTO = handleCategoryRequests(requestMessage);
        } else if (messagePath.contains("tag")) {
            System.out.println("Going to tag.");
            responseMessageDTO = handleTagRequests(requestMessage);
        } else {
            log.warn("Unknown message path: {}", requestMessage.getPath());
            responseMessageDTO = new ResponseMessageDTO(requestMessage.getId(), 404, "Path does not exist.");
        }
        return responseMessageDTO;
    }
    public ResponseMessageDTO handleProductRequests(RequestMessageDTO requestMessageDTO) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO;
        switch (requestMessageDTO.getMethod()) {
            case "GET":
                switch (requestMessageDTO.getPath()) {
                    case "/product":
                        Integer readProductId = objectMapper.readValue(requestMessageDTO.getBody(), Integer.class);
                        log.info("Received read request for product with id: {}", readProductId);
                        responseMessageDTO = productService.handleReadProduct(requestMessageDTO.getId(), readProductId);
                        break;
                    case "/product/search":
                        ProductSearchRequestDTO productSearchRequestDTO = objectMapper.readValue(requestMessageDTO.getBody(), ProductSearchRequestDTO.class);
                        log.info("Received search request: {}", requestMessageDTO.getBody());
                        responseMessageDTO = productService.handleSearchProduct(requestMessageDTO.getId(), productSearchRequestDTO);
                        break;
                    default:
                        log.warn("Unknown message path: {}", requestMessageDTO.getPath());
                        responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, null);
                        break;
                }
                break;
            case "POST":
                ProductCreationRequestDTO productCreationRequestDTO = objectMapper.readValue(requestMessageDTO.getBody(), ProductCreationRequestDTO.class);
                log.info("Received product: {}", productCreationRequestDTO);
                responseMessageDTO = productService.handleCreateProduct(requestMessageDTO.getId(), productCreationRequestDTO);
                break;
            case "PUT":
                ProductUpdateRequestDTO productUpdate = objectMapper.readValue(requestMessageDTO.getBody(), ProductUpdateRequestDTO.class);
                log.info("Received product update: {}", productUpdate);
                responseMessageDTO = productService.handleUpdateProduct(requestMessageDTO.getId(), productUpdate);
                break;
            case "DELETE":
                ProductDeleteRequestDTO productDeleteRequestDTO = objectMapper.readValue(requestMessageDTO.getBody(), ProductDeleteRequestDTO.class);
                log.info("Received delete request for product with id: {}", productDeleteRequestDTO);
                responseMessageDTO = productService.handleDeleteProduct(requestMessageDTO.getId(), productDeleteRequestDTO);
                break;
            default:
                log.warn("Unknown message type: {}", requestMessageDTO.getMethod());
                responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, null);
                break;
        }
        return responseMessageDTO;
    }

    public ResponseMessageDTO handleCategoryRequests(RequestMessageDTO requestMessageDTO) throws JsonProcessingException {
        ResponseMessageDTO responseMessageDTO = null;
        switch (requestMessageDTO.getMethod()) {
            case "GET":
                switch (requestMessageDTO.getPath()) {
                    case "/category/search":
                        log.info("Read body into Category Search Request");
                        CategorySearchRequestDTO categorySearchRequestDTO = objectMapper.readValue(
                            requestMessageDTO.getBody(),
                            CategorySearchRequestDTO.class
                        );
                        log.info("Received Search Request: {}", categorySearchRequestDTO.getQuery());
                        responseMessageDTO = categoryService.handleSearchCategory(
                            requestMessageDTO.getId(),
                            categorySearchRequestDTO
                        );
                        break;
                    case "/category":
                        log.info("Read body into Category Read Request");
                        CategoryReadRequestDTO categoryReadRequestDTO = objectMapper.readValue(
                            requestMessageDTO.getBody(),
                            CategoryReadRequestDTO.class
                        );
                        log.info("Received Read Request for category: {}", categoryReadRequestDTO.getCategoryName());
                        responseMessageDTO = categoryService.handleReadCategory(
                            requestMessageDTO.getId(),
                            categoryReadRequestDTO
                        );
                        break;
                    default:
                        log.warn("Unknown message path: {}", requestMessageDTO.getPath());
                        responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, "Unknown message path.");
                        break;
                }
                break;
            case "POST":
                log.info("Read body into Category Create Request");
                CategoryCreationRequestDTO categoryCreationRequestDTO = objectMapper.readValue(
                    requestMessageDTO.getBody(),
                    CategoryCreationRequestDTO.class
                );
                log.info("Received Create Request for category: {}", categoryCreationRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleCreateCategory(
                    requestMessageDTO.getId(),
                    categoryCreationRequestDTO
                );
                break;
            case "PUT":
                log.info("Read body into Category Update Request");
                CategoryUpdateRequestDTO categoryUpdateRequestDTO = objectMapper.readValue(
                    requestMessageDTO.getBody(),
                    CategoryUpdateRequestDTO.class
                );
                log.info("Received Update Request for category: {}", categoryUpdateRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleUpdateCategory(
                    requestMessageDTO.getId(),
                    categoryUpdateRequestDTO
                );
                break;
            case "DELETE":
                log.info("Read body into Category Delete Request");
                CategoryDeleteRequestDTO categoryDeleteRequestDTO = objectMapper.readValue(
                    requestMessageDTO.getBody(),
                    CategoryDeleteRequestDTO.class
                );
                log.info("Received Delete Request for category: {}", categoryDeleteRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleDeleteCategory(
                    requestMessageDTO.getId(),
                    categoryDeleteRequestDTO
                );
                break;
            default:
                log.warn("Unknown message type: {}", requestMessageDTO.getMethod());
                responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, "Unknown message path.");
                break;
        }
        return responseMessageDTO;
    }

    public ResponseMessageDTO handleTagRequests(RequestMessageDTO requestMessageDTO) {
        return new ResponseMessageDTO(requestMessageDTO.getId(), 404, null);
    }
}
