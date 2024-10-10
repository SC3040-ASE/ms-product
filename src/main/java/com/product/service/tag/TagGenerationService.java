package com.product.service.tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.product.dto.ResponseMessageDTO;
import com.product.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class TagGenerationService {
    @Value("${gemini.api.key}")
    private String apiKey;
    private final ObjectMapper objectMapper;
    private final String BASE_URL;
    private final String systemInstructionTextTemplate;
    private final RestTemplate restTemplate;
    private final TagRepository tagRepository;

    @Autowired
    public TagGenerationService(ObjectMapper objectMapper, TagRepository tagRepository){
        this.objectMapper = objectMapper;
        this.tagRepository = tagRepository;
        BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
        restTemplate = new RestTemplateBuilder().build();
        systemInstructionTextTemplate = "You are tasked with generating a list of tags based on a product's name and description. " +
                "The tags should capture key characteristics and concepts that are closely related to the product, reflecting its most relevant features. " +
                "Generate at least of 10 tags. " +
                "Here are some of the existing tags in database, prioritize the tags in this list:" +
                "%s \n"+
                "return the answer using this JSON schema:" +
                "list[str]";

    }

    public ResponseMessageDTO generateTag(String messageId, String productName, String productDescription, Integer categoryId) throws Exception {
        String prompt = String.format("Product name: %s, Product Description: %s", productName, productDescription);
        List<String> existingTags = tagRepository.searchTag(categoryId,prompt);

        List<String> tagList = generateTagUsingLLM(prompt, existingTags);

        List<String> smallLetterTagList = tagList.stream().map(String::toLowerCase).toList();

        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(smallLetterTagList));
    }


    public List<String> generateTagUsingLLM(String prompt, List<String> existingList) throws Exception {
        String apiUrl = BASE_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ObjectMapper objectMapper = new ObjectMapper();

        // content
        ObjectNode contentNode = objectMapper.createObjectNode();
        ObjectNode contentPartsNode = objectMapper.createObjectNode();
        contentPartsNode.put("text", prompt);
        contentNode.set("parts", contentPartsNode);


        // system_instruction
        String systemInstructionText = String.format(systemInstructionTextTemplate, objectMapper.writeValueAsString(existingList));
        ObjectNode systemInstructionNode = objectMapper.createObjectNode();
        ObjectNode systemInstructionPartsNode = objectMapper.createObjectNode();
        systemInstructionPartsNode.put("text", systemInstructionText);
        systemInstructionNode.set("parts", systemInstructionPartsNode);

        // generation config
        ObjectNode generationConfig = objectMapper.createObjectNode();
        generationConfig.put("response_mime_type", "application/json");

        // request body
        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        requestBodyNode.set("contents", contentNode);
        requestBodyNode.set("system_instruction", systemInstructionNode);
        requestBodyNode.set("generationConfig", generationConfig);

        String requestBody;
        requestBody = objectMapper.writeValueAsString(requestBodyNode);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        String text = jsonNode
                .path("candidates")
                .get(0).path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        return objectMapper.readValue(text, new TypeReference<>() {
        });
    }



}
