package com.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.product.dto.*;
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
            try {
                RequestMessageDTO requestMessage = objectMapper.readValue(payload, RequestMessageDTO.class);
                log.info("Received message: {}", requestMessage);
                String messageMethod = requestMessage.getMethod();
                String requestBody = requestMessage.getBody();
                String responseId = requestMessage.getId();

                ResponseMessageDTO responseMessage;
                switch (messageMethod) {
                    case "create-product":
                        ProductCreationRequestDTO productCreationRequestDTO = objectMapper.readValue(requestBody, ProductCreationRequestDTO.class);
                        log.info("Received product: {}", productCreationRequestDTO);
                        responseMessage = productService.handleCreateProduct(responseId, productCreationRequestDTO);
                        break;
                    case "read-product":
                        Integer readProductId = objectMapper.readValue(requestBody, Integer.class);
                        log.info("Received read request for product with id: {}", readProductId);
                        responseMessage = productService.handleReadProduct(responseId, readProductId);
                        break;
                    case "search-product":
                        ProductSearchRequestDTO productSearchRequestDTO = objectMapper.readValue(requestBody, ProductSearchRequestDTO.class);
                        log.info("Received search request: {}", requestBody);
                        responseMessage = productService.handleSearchProduct(responseId, productSearchRequestDTO);
                        break;
                    case "delete-product":
                        ProductDeleteRequestDTO productDeleteRequestDTO = objectMapper.readValue(requestBody, ProductDeleteRequestDTO.class);
                        log.info("Received delete request for product with id: {}", productDeleteRequestDTO);
                        responseMessage = productService.handleDeleteProduct(responseId, productDeleteRequestDTO);
                        break;
                    case "update-product":
                        ProductUpdateRequestDTO productUpdate = objectMapper.readValue(requestBody, ProductUpdateRequestDTO.class);
                        log.info("Received product update: {}", productUpdate);
                        responseMessage = productService.handleUpdateProduct(responseId, productUpdate);
                        break;
                    default:
                        log.warn("Unknown message type: {}", messageMethod);
                        responseMessage = new ResponseMessageDTO(responseId, 404, null);
                        break;
                }

                String responsePayload = objectMapper.writeValueAsString(responseMessage);
                messagingGateway.sendToPubSub(responsePayload);

            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            } finally {
                BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
                originalMessage.ack();
            }
        };
    }
}
