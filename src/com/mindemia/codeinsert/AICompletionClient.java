/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

/**
 *
 * @author rickard
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class AICompletionClient {
    private final String API_URL;
    private final String API_KEY;
    private final String MODEL;
    private final int MAX_TOKENS;
    private final String systemPrompt;
    private final String tools;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AICompletionClient(String apiUrl, String apiKey, String model, int maxTokens, String systemPrompt, String tools) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.API_URL = apiUrl;
        this.API_KEY = apiKey;
        this.MODEL = model;
        this.MAX_TOKENS = maxTokens;
        this.systemPrompt = systemPrompt;
        this.tools = tools;
    }

    public String fetchSuggestion(String prompt, String toolChoice) {
        try {
            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", MODEL);
            //requestBody.put("prompt", prompt);
            requestBody.put("messages", messages);
//            requestBody.put("tools", tools);
//            if (!toolChoice.isEmpty()) {
//                requestBody.put("tool_choice", toolChoice);
//            }

            requestBody.put("max_tokens", MAX_TOKENS);

            // Convert to JSON string
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "/* Error fetching AI completion */";
        }
    }

    private String parseResponse(String responseBody) {
        System.out.println("response " + responseBody);
        if(responseBody == null || responseBody.isEmpty()) {
            return "";
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.get("choices");
            if(choices.size() < 1) {
                System.out.println("choices are empty ");
                return "/* AI response empty */";
            }
            var message = choices.get(0).get("message");
            if(message != null) {
                if (message.get("tool_calls") != null) {
                    System.out.println("tool_calls " + message.get("tool_calls"));
                }
                if (message.get("content") != null) {
                    return message.get("content").asText("").strip();
                }
            }
            if(choices.get(0).get("text") != null) {
                return choices.get(0).get("text").asText("").strip();
            }
            return "/* AI response empty */";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "/* Error parsing AI response */";
        }
    }
}
