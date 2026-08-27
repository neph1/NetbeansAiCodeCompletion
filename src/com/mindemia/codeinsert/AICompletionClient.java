/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

/**
 *
 * @author rickard
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class AICompletionClient {
    private final String API_URL;
    private final String API_KEY;
    private final String MODEL;
    private final int MAX_TOKENS;
    private final String systemPrompt;
    private final ArrayNode tools;

    public final HttpClient httpClient;
    public final ObjectMapper objectMapper;
    public final ResponseParser responseParser;

    public AICompletionClient(String apiUrl, String apiKey, String model, int maxTokens, String systemPrompt, ArrayNode tools) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.API_URL = apiUrl;
        this.API_KEY = apiKey;
        this.MODEL = model;
        this.MAX_TOKENS = maxTokens;
        this.systemPrompt = systemPrompt;
        this.tools = tools;
        this.responseParser = new ResponseParser();
    }

    public String fetchSuggestion(String prompt, String toolChoice) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest request = new ChatCompletionRequest(
                MODEL,
                messages,
                tools,
                MAX_TOKENS
            );

            String jsonRequest = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return responseParser.parseResponse(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "/* Error fetching AI completion */";
        }
    }

    private static class ChatMessage {
        @JsonProperty("role")
        public String role;
        @JsonProperty("content")
        public String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private static class ChatCompletionRequest {
        @JsonProperty("model")
        public String model;
        @JsonProperty("messages")
        public List<ChatMessage> messages;
        @JsonProperty("tools")
        public ArrayNode tools;
        @JsonProperty("max_tokens")
        public int max_tokens;

        public ChatCompletionRequest(String model, List<ChatMessage> messages, ArrayNode tools, int max_tokens) {
            this.model = model;
            this.messages = messages;
            this.tools = tools;
            this.max_tokens = max_tokens;
        }
    }
}
