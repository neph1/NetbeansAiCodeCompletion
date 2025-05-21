/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.chat;

import com.mindemia.codeinsert.AICompletionClient;
import com.mindemia.codeinsert.AICompletionOptionsPanel;
import com.mindemia.codeinsert.OpenFileContextCollector;
import com.mindemia.codeinsert.tools.EditorUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.JTextComponent;
import org.openide.util.*;

/**
 *
 * @author rickard
 */
public class AiChatClient extends AICompletionClient {

    private static final String API_URL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("host_chat", "http://127.0.0.1:8080/v1/completions");
    private static final String API_KEY = NbPreferences.forModule(AICompletionOptionsPanel.class).get("api_key", "");
    private static final String MODEL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("model_chat", "gpt-4");
    private static final int MAX_TOKENS = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("max_tokens", 300);
    private static final String SYSTEM_PROMPT = NbPreferences.forModule(AICompletionOptionsPanel.class).get("system_prompt_chat", "");
    
    private static final String tools = """
                                 [
                                     {
                                         "type": "function",
                                         "function": {
                                             "name": "insert_code",
                                             "description": "Inserts Java code into the document for the user",
                                             "parameters":{
                                                 "imports": ["required imports"],
                                                 "methods": [
                                                 {
                                                     "modifier": "public/protected/private",
                                                     "name": "name of method",
                                                     "params": [{
                                                     "name": "parameter name",
                                                     "type": "type of parameter, ie String, int, etc"
                                                     }],
                                                     "body": "code",
                                                     "returnType": "void",
                                                     "throws": []
                                                 }
                                             ]
                                             }
                                         }
                                     }
                                 ]
                                 """;
    
    private static final String toolChoice = """
                                             tool_choice={
                                                 "type": "function",
                                                 "function": {"name": "insert_code"}
                                             }
                                             """;

    final Pattern pattern = Pattern.compile("(?s)```(?:json)?\\s*(\\{.*?\\})\\s*```");

    final static String chatTemplate = """
                                  <|im_start|>system
                                  %s<|im_end|>
                                  <|im_start|>user
                                  %s<|im_end|>
                                  <|im_start|>assistant
                                  """;

    final static String singleUser = """
                                  <|im_start|>user
                                  %s<|im_end|>
                                  """;
    final static String singleAssistant = """
                                  <|im_start|>assistant
                                  %s<|im_end|>
                                  """;

    private List<String> history = new ArrayList<>();

    public AiChatClient() {
        super(API_URL, API_KEY, MODEL, MAX_TOKENS, SYSTEM_PROMPT, tools);
    }

    public String fetchSuggestion(String prompt) {
        history.add(String.format(singleUser, prompt));
        final String response = super.fetchSuggestion(constructChatPrompt(EditorUtils.getActiveTextComponent().orElse(null), prompt), toolChoice);
        extractJson(response);
        history.add(String.format(singleAssistant, response));
        return response;
    }

    private String constructChatPrompt(JTextComponent code, String userPrompt) {
        StringBuilder builder = new StringBuilder();
        
        if(code != null) {
            StringBuilder snippetsBuilder = new StringBuilder();
            try {
                List<String> snippets = OpenFileContextCollector.collectContextFromOpenFiles(code);
                
                
                for(String s: snippets) {
                    snippetsBuilder.append(s);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }            
            if(!snippetsBuilder.isEmpty()) {
                builder.append(String.format("<snippets>%s</snippets>\n", snippetsBuilder.toString()));
            }
            String allCode = code.getText();
            builder.append(String.format("<code>%s</code>\n", allCode.split("package")[1]));

        }
        
        for(String s: history) {
            builder.append(s);
        }
        
        builder.append(String.format(chatTemplate, SYSTEM_PROMPT.replace("\"", "\\\""), userPrompt));
        System.out.println("prompt: " + builder.toString());
        return builder.toString();
    }

    private void extractJson(String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String json = matcher.group(1);
            System.out.println("found json " + json);
        }
    }
}
