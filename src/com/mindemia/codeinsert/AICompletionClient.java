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
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.openide.util.NbPreferences;

public class AICompletionClient {
    private final String API_URL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("host", "http://127.0.0.1:8080/v1/completions");
    private final String API_KEY = NbPreferences.forModule(AICompletionOptionsPanel.class).get("api_key", "");
    private final String MODEL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("model", "gpt-4");
    private final int MAX_TOKENS = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("max_tokens", 300);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AICompletionClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String fetchSuggestion(String prompt) {
        try {
            System.out.println("createTask");
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", MODEL);
            requestBody.put("prompt", prompt);
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
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.get("content").asText("/* No AI response */");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "/* Error parsing AI response */";
        }
    }
}
