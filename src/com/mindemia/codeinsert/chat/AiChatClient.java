/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.chat;

import com.mindemia.codeinsert.AICompletionClient;
import com.mindemia.codeinsert.AICompletionOptionsPanel;
import com.mindemia.codeinsert.OpenFileContextCollector;
import com.mindemia.codeinsert.tools.EditorUtils;
import com.mindemia.codeinsert.tools.ToolJsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                                     {
                                         "type": "function",
                                         "function": {
                                             "name": "insert_code",
                                             "description": "Inserts Java code into the document for the user",
                                             "arguments":{
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
                                 """;

    private static final String toolChoice = """
                                             {
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

    private Map<String, List<String>> history = new HashMap<>();

    public AiChatClient() {
        super(API_URL, API_KEY, MODEL, MAX_TOKENS, SYSTEM_PROMPT, new ToolJsonBuilder().createToolsTemplate());
    }

    public String fetchSuggestion(String selectedTab, String prompt, Map<String, JTextComponent> selectedContext) {
        var list = history.getOrDefault(selectedTab, new ArrayList<>());
        list.add(String.format(singleUser, prompt));
        history.put(selectedTab, list);
        JTextComponent focusedComponent = EditorUtils.getActiveTextComponent().orElse(null);

        final String context = constructChatPrompt(selectedTab, focusedComponent, prompt, selectedContext);

        final String response = super.fetchSuggestion(context, "");
        list.add(String.format(singleAssistant, response));
        history.put(selectedTab, list);
        return response;
    }

    private String constructChatPrompt(String selectedName, JTextComponent selectedTab, String userPrompt, Map<String, JTextComponent> selectedContext) {
        selectedContext.remove(selectedName);
        StringBuilder builder = new StringBuilder();
        try {
            String repository = new String(Files.readAllBytes(Paths.get("repository.md")));
            builder.append(String.format("<repository>%s</repository>\n", repository));
        } catch (NoSuchFileException ex) {
            // fail silentry
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (selectedTab != null) {
            StringBuilder snippetsBuilder = new StringBuilder();
            try {
                List<String> snippets = OpenFileContextCollector.collectContextFromOpenFiles(selectedTab, selectedContext.keySet());

                for (String s : snippets) {
                    snippetsBuilder.append(s);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (!snippetsBuilder.isEmpty()) {
                builder.append(String.format("<snippets>%s</snippets>\n", snippetsBuilder.toString()));
            }
            builder.append("<code>");
            selectedContext.forEach( (name, tab) -> {
                String code = getCode(tab);
                builder.append(String.format("%s:\n%s\n", name, code));
            });
            
            String code = getCode(selectedTab);
            builder.append(String.format("%s:\n%s\n", selectedName, code));
            builder.append("</code>");
            

        }

        for (String s : history.get(selectedName)) {
            builder.append(s);
        }

        return builder.append(String.format(chatTemplate, SYSTEM_PROMPT.replace("\"", "\\\""), userPrompt)).toString();
        //System.out.println("prompt: " + userPrompt);
        //return userPrompt;
    }

    private String getCode(JTextComponent tab) {
        String allCode = tab.getText();
        // don't send license in java files
        if (allCode.contains("package")) {
            allCode = allCode.split("package")[1];
        }
        return allCode;
    }

}
