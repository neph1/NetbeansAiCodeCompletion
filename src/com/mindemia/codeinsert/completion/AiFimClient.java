/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.completion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mindemia.codeinsert.AICompletionClient;
import com.mindemia.codeinsert.AICompletionOptionsPanel;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.openide.util.NbPreferences;

/**
 *
 * @author rickard
 */
public class AiFimClient extends AICompletionClient{
    
    private static final String API_URL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("host_fim", "http://127.0.0.1:8080/v1/completions");
    private static final String API_KEY = NbPreferences.forModule(AICompletionOptionsPanel.class).get("api_key", "");
    private static final String MODEL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("model_fim", "gpt-4");
    private static final int MAX_TOKENS = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("max_tokens", 300);
    private static final String SYSTEM_PROMPT = NbPreferences.forModule(AICompletionOptionsPanel.class).get("system_prompt_fim", "");

    public AiFimClient() {
        super(API_URL, API_KEY, MODEL, MAX_TOKENS, SYSTEM_PROMPT, null);
    }
        
    public String fetchSuggestion(String prompt, String toolChoice, String suffix) {
        
        try {
            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode data = objectMapper.createObjectNode();
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            
            requestBody.put("prompt", prompt);
            requestBody.put("temperature", 0);
            requestBody.put("suffix", suffix);
            requestBody.put("stop", "[\"\\n\\n\"]");
            requestBody.put("max_tokens", 300);
            requestBody.put("model", MODEL);
           
            // Convert to JSON string
            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return responseParser.parseResponse(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "/* Error fetching AI completion */";
        }
    }
}
