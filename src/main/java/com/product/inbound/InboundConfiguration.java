package com.product.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.product.config.PubSubConfiguration;
import com.product.dto.ProductReceived;
import com.product.dto.ResponseObject;
import com.product.dto.SearchQuery;
import com.product.outbound.OutboundConfiguration;
import com.product.service.ProductService;
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
            String messageType = (String) message.getHeaders().get("type");
            String payload = new String((byte[]) message.getPayload());

            try {
                ResponseObject response;
                switch (messageType) {
                    case "create-product":
                        ProductReceived productReceived = objectMapper.readValue(payload, ProductReceived.class);
                        log.info("Received product: {}", productReceived);
                        response = productService.handleCreateProduct(productReceived);
                        break;
                    case "search-product":
                        SearchQuery searchQuery = objectMapper.readValue(payload, SearchQuery.class);
                        log.info("Received search request: {}", payload);
                        response = productService.handleSearchProduct(searchQuery);
                        break;
                    case "delete-product":
                        Long productId = objectMapper.readValue(payload, Long.class);
                        log.info("Received delete request for product with id: {}", productId);
                        response = productService.handleDeleteProduct(productId);
                        break;
                    case "update-product":
                        ProductReceived productUpdate = objectMapper.readValue(payload, ProductReceived.class);
                        log.info("Received product update: {}", productUpdate);
                        response = productService.handleUpdateProduct(productUpdate);
                        break;
                    default:
                        log.warn("Unknown message type: {}", messageType);
                        response = new ResponseObject("Unknown message type", null);
                        break;
                }

                String responsePayload = objectMapper.writeValueAsString(response);
                messagingGateway.sendToPubSub(responsePayload);

            } catch (Exception e) {
                log.error("Error processing message", e);
            } finally {
                BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
                originalMessage.ack();
            }
        };
    }
}
