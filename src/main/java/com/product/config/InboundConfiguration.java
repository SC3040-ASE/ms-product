package com.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.product.dto.*;
import com.product.dto.category.*;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.product.ProductUpdateRequestDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.*;
import com.product.service.category.CategoryService;
import com.product.service.product.ProductService;
import com.product.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InboundConfiguration {
    private final PubSubConfiguration pubSubConfiguration;
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final OutboundConfiguration.PubsubOutboundGateway messagingGateway;

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
                pubSubConfiguration.getSubscription());
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
                ResponseMessageDTO responseMessage = handler(requestMessage);

                String responsePayload = objectMapper.writeValueAsString(responseMessage);
                // Do we need different topics for product / category / tag?
                messagingGateway.sendToPubSub(responsePayload);
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                try {
                    ResponseMessageDTO responseMessage = new ResponseMessageDTO(responseId, 500,
                            "Internal server error");
                    String responsePayload = objectMapper.writeValueAsString(responseMessage);
                    messagingGateway.sendToPubSub(responsePayload);
                } catch (Exception ex) {
                    log.error("Error: {}", ex.getMessage());
                }
            } finally {
                BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
                        .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
                originalMessage.ack();
            }
        };
    }

    public ResponseMessageDTO handler(RequestMessageDTO requestMessage) throws Exception {
        String messagePath = requestMessage.getPath();
        ResponseMessageDTO responseMessageDTO;
        if (messagePath.contains("products")) {
            if(messagePath.contains("products/tag")){
                log.info("Going to tag.");
                responseMessageDTO = handleTagRequests(requestMessage);
            }else if(messagePath.contains("products/category")){
                log.info("Going to category.");
                responseMessageDTO = handleCategoryRequests(requestMessage);
            }else{
                log.info("Going to product.");
                responseMessageDTO = handleProductRequests(requestMessage);
            }
        } else {
            log.warn("Unknown message path: {}", requestMessage.getPath());
            responseMessageDTO = new ResponseMessageDTO(requestMessage.getId(), 404, "Path does not exist.");
        }
        return responseMessageDTO;
    }

    public ResponseMessageDTO handleProductRequests(RequestMessageDTO requestMessageDTO) throws Exception {
        log.info("Handling product request: {}", requestMessageDTO);
        ResponseMessageDTO responseMessageDTO;
        Map<String, String> headers = requestMessageDTO.getHeaders();
        Integer ownerId = objectMapper.readValue(headers.get("X-User-Id"), Integer.class);
        Boolean isAdmin = objectMapper.readValue(headers.get("X-Is-Admin"), Boolean.class);

        switch (requestMessageDTO.getMethod()) {
            case "GET":
                switch (requestMessageDTO.getPath()) {
                    case "/products":
                        if (!headers.containsKey("X-id")) {
                            log.info("Missing id");
                            responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 400, "Bad request");
                            break;
                        }
                        Integer readProductId = objectMapper.readValue(headers.get("X-id"), Integer.class);
                        responseMessageDTO = productService.handleReadProduct(requestMessageDTO.getId(), readProductId);
                        break;
                    case "/products/owner":
                        if (!headers.containsKey("X-startIndex") || !headers.containsKey("X-endIndex") || !headers.containsKey("X-sellerId")) {
                            log.info("Missing startIndex, endIndex or sellerId");
                            responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 400, "Bad Request");
                            break;
                        }
                        Integer startIndex = objectMapper.readValue(headers.get("X-startIndex"), Integer.class);
                        Integer endIndex = objectMapper.readValue(headers.get("X-endIndex"), Integer.class);
                        Integer sellerId = objectMapper.readValue(headers.get("X-sellerId"), Integer.class);
                        responseMessageDTO = productService.handleReadProductsByOwnerId(requestMessageDTO.getId(),
                                sellerId, startIndex, endIndex);
                        break;
                    case "/products/reserved":
                        responseMessageDTO = productService.handleReadProductsReserved(requestMessageDTO.getId(), ownerId);
                        break;
                    case "/products/searchRange":
                        if (!headers.containsKey("X-query") || !headers.containsKey("X-startRank")
                                || !headers.containsKey("X-endRank")) {
                            log.info("Missing query, startRank or endRank");
                            responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 400, "Bad Request");
                            break;
                        }
                        String searchRangeQuery = headers.get("X-query");
                        Integer startRank = objectMapper.readValue(headers.get("X-startRank"), Integer.class);
                        Integer endRank = objectMapper.readValue(headers.get("X-endRank"), Integer.class);
                        responseMessageDTO = productService.handleSearchRangeProduct(requestMessageDTO.getId(),
                                searchRangeQuery, startRank, endRank);
                        break;
                    default:
                        log.warn("Unknown message path: {}", requestMessageDTO.getPath());
                        responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, null);
                        break;
                }
                break;
            case "POST":
                ProductCreationRequestDTO productCreationRequestDTO = objectMapper
                        .readValue(requestMessageDTO.getBody(), ProductCreationRequestDTO.class);
                productCreationRequestDTO.setOwnerId(ownerId);
                log.info("Received product: {}", productCreationRequestDTO);
                responseMessageDTO = productService.handleCreateProduct(requestMessageDTO.getId(),
                        productCreationRequestDTO);
                break;
            case "PUT":
                ProductUpdateRequestDTO productUpdate = objectMapper.readValue(requestMessageDTO.getBody(),
                        ProductUpdateRequestDTO.class);
                productUpdate.setOwnerId(ownerId);
                productUpdate.setIsAdmin(isAdmin);
                log.info("Received product update: {}", productUpdate);
                responseMessageDTO = productService.handleUpdateProduct(requestMessageDTO.getId(), productUpdate);
                break;
            case "DELETE":
                if (!headers.containsKey("X-id")) {
                    log.info("Missing id");
                    responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 400, "Bad request");
                    break;
                }
                Integer deleteProductId = objectMapper.readValue(headers.get("X-id"), Integer.class);
                log.info("Received delete request for product with id: {}", deleteProductId);
                responseMessageDTO = productService.handleDeleteProduct(requestMessageDTO.getId(), deleteProductId,
                        ownerId, isAdmin);
                break;
            default:
                log.warn("Unknown message type: {}", requestMessageDTO.getMethod());
                responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, null);
                break;
        }
        return responseMessageDTO;
    }

    public ResponseMessageDTO handleCategoryRequests(RequestMessageDTO requestMessageDTO) throws Exception {
        String unknownPath = "Unknown message path";
        ResponseMessageDTO responseMessageDTO;
        switch (requestMessageDTO.getMethod()) {
            case "GET":
                switch (requestMessageDTO.getPath()) {
                    case "/products/category":
                        log.info("Read body into Category Read Request");
                        CategoryReadRequestDTO categoryReadRequestDTO = objectMapper.readValue(
                                requestMessageDTO.getBody(),
                                CategoryReadRequestDTO.class);
                        log.info("Received Read Request for category: {}", categoryReadRequestDTO.getCategoryName());
                        responseMessageDTO = categoryService.handleReadCategory(
                                requestMessageDTO.getId(),
                                categoryReadRequestDTO);
                        break;
                    default:
                        log.warn("{}: {}", unknownPath, requestMessageDTO.getPath());
                        responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404,
                                unknownPath);
                        break;
                }
                break;
            case "POST":
                log.info("Read body into Category Create Request");
                CategoryCreationRequestDTO categoryCreationRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        CategoryCreationRequestDTO.class);
                log.info("Received Create Request for category: {}", categoryCreationRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleCreateCategory(
                        requestMessageDTO.getId(),
                        categoryCreationRequestDTO);
                break;
            case "PUT":
                log.info("Read body into Category Update Request");
                CategoryUpdateRequestDTO categoryUpdateRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        CategoryUpdateRequestDTO.class);
                log.info("Received Update Request for category: {}", categoryUpdateRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleUpdateCategory(
                        requestMessageDTO.getId(),
                        categoryUpdateRequestDTO);
                break;
            case "DELETE":
                log.info("Read body into Category Delete Request");
                CategoryDeleteRequestDTO categoryDeleteRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        CategoryDeleteRequestDTO.class);
                log.info("Received Delete Request for category: {}", categoryDeleteRequestDTO.getCategoryName());
                responseMessageDTO = categoryService.handleDeleteCategory(
                        requestMessageDTO.getId(),
                        categoryDeleteRequestDTO);
                break;
            default:
                log.warn("Unknown message type: {}", requestMessageDTO.getMethod());
                responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, "Unknown message type");
                break;
        }
        return responseMessageDTO;
    }

    public ResponseMessageDTO handleTagRequests(RequestMessageDTO requestMessageDTO) throws Exception {
        String unknownPath = "Unknown message path";
        ResponseMessageDTO responseMessageDTO;
        Map<String, String> headers = requestMessageDTO.getHeaders();
        switch (requestMessageDTO.getMethod()) {
            case "GET":
                switch (requestMessageDTO.getPath()) {
                    case "/products/tag":
                        log.info("Read body into Tag Read Request");
                        TagReadRequestDTO tagReadRequestDTO = objectMapper.readValue(
                                requestMessageDTO.getBody(),
                                TagReadRequestDTO.class);
                        log.info("Received Read Request for tag: {}", tagReadRequestDTO.getTagName());
                        responseMessageDTO = tagService.handleReadTag(
                                requestMessageDTO.getId(),
                                tagReadRequestDTO);
                        break;
                    case "/products/tag/generate":
                        if (!headers.containsKey("X-productName") || !headers.containsKey("X-productDescription") || !headers.containsKey("X-categoryId")) {
                            log.info("Missing productName, productDescription or categoryId");
                            responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 400, "Bad Request");
                            break;
                        }
                        String productName = headers.get("X-productName");
                        String productDescription = headers.get("X-productDescription");
                        Integer categoryId = objectMapper.readValue(headers.get("X-categoryId"),Integer.class);
                        responseMessageDTO = tagService.handleGenerateTag(requestMessageDTO.getId(), productName, productDescription, categoryId);
                        break;
                    default:
                        log.warn("{}: {}",unknownPath, requestMessageDTO.getPath());
                        responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404,
                                unknownPath);
                        break;
                }
                break;
            case "POST":
                log.info("Read body into Tag Create Request");
                TagCreationRequestDTO tagCreationRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        TagCreationRequestDTO.class);
                log.info("Received Create Request for tag: {}", tagCreationRequestDTO.getTagName());
                responseMessageDTO = tagService.handleCreateTag(
                        requestMessageDTO.getId(),
                        tagCreationRequestDTO);
                break;
            case "PUT":
                log.info("Read body into Tag Update Request");
                TagUpdateRequestDTO tagUpdateRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        TagUpdateRequestDTO.class);
                log.info("Received Update Request for tag: {}", tagUpdateRequestDTO.getTagName());
                responseMessageDTO = tagService.handleUpdateTag(
                        requestMessageDTO.getId(),
                        tagUpdateRequestDTO);
                break;
            case "DELETE":
                log.info("Read body into Tag Delete Request");
                TagDeleteRequestDTO tagDeleteRequestDTO = objectMapper.readValue(
                        requestMessageDTO.getBody(),
                        TagDeleteRequestDTO.class);
                log.info("Received Delete Request for tag: {}", tagDeleteRequestDTO.getTagName());
                responseMessageDTO = tagService.handleDeleteTag(
                        requestMessageDTO.getId(),
                        tagDeleteRequestDTO);
                break;
            default:
                log.warn("Unknown message type: {}", requestMessageDTO.getMethod());
                responseMessageDTO = new ResponseMessageDTO(requestMessageDTO.getId(), 404, "Unknown message type");
                break;
        }
        return responseMessageDTO;
    }
}
