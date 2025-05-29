/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.instruct;

import com.mindemia.codeinsert.AICompletionClient;
import com.mindemia.codeinsert.AICompletionOptionsPanel;
import com.mindemia.codeinsert.OpenFileContextCollector;
import com.mindemia.codeinsert.tools.EditorUtils;
import java.io.IOException;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author rickard
 */
public class AiInstructClient extends AICompletionClient{
    
    private static final String API_URL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("host_instruct", "http://127.0.0.1:8080/v1/completions");
    private static final String API_KEY = NbPreferences.forModule(AICompletionOptionsPanel.class).get("api_instruct", "");
    private static final String MODEL = NbPreferences.forModule(AICompletionOptionsPanel.class).get("model_instruct", "gpt-4");
    private static final int MAX_TOKENS = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("max_tokens", 300);
    private static final String SYSTEM_PROMPT = NbPreferences.forModule(AICompletionOptionsPanel.class).get("system_prompt_instruct", "");
    
    final static String chatTemplate = """
                              <|im_start|>system
                              %s<|im_end|>
                              <|im_start|>user
                              %s<|im_end|>
                              <|im_start|>assistant
                              """;

    
    public AiInstructClient() {
        super(API_URL, API_KEY, MODEL, MAX_TOKENS, SYSTEM_PROMPT, "");
    }
    
    public String fetchSuggestion(String prompt) {
        final String response = super.fetchSuggestion(constructChatPrompt(EditorUtils.getActiveTextComponent().orElse(null), prompt), null);
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
        
        builder.append(String.format(chatTemplate, SYSTEM_PROMPT.replace("\"", "\\\""), userPrompt));
        System.out.println("prompt: " + builder.toString());
        return builder.toString();
    }
}
