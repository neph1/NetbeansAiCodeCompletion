/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools;

/**
 *
 * @author rickard
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.mindemia.codeinsert.data.GitPatchResponse;
import com.mindemia.codeinsert.data.InsertCodeToolCall;
import com.mindemia.codeinsert.tools.gitpatch.GitPatchApplier;
import java.net.http.HttpClient;

public class ToolJsonBuilder {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ToolJsonBuilder() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    public ArrayNode createToolsTemplate() {
        ArrayNode tools = objectMapper.createArrayNode();
        
        tools.add(buildGitPatchTool());
        
        return tools;
    }
    
    public GitPatchResponse parseGitPatchResponse(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(responseBody, GitPatchResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ObjectNode buildGitPatchTool() {
        
        ObjectNode props = objectMapper.createObjectNode();
        ObjectNode patchProp = objectMapper.createObjectNode();
        patchProp.put("type", "string");
        patchProp.put("description", "The generated code as a git patch");
        props.set("patch", patchProp);
        
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put("type", "object");
        parameters.set("properties", props);
        parameters.set("required", objectMapper.createArrayNode().add("patch"));
        
        ObjectNode function = objectMapper.createObjectNode();
        function.put("name", "git_patch");
        function.put("description", "Create a git patch with code for the user.");
        function.set("parameters", parameters);
        
        // top-level object
        ObjectNode tool = objectMapper.createObjectNode();
        tool.put("type", "function");
        tool.set("function", function);
        return tool;
    }
    
    public ObjectNode buildInsertCodeTool() {

        // "params" array
        ObjectNode param = objectMapper.createObjectNode();
        param.put("name", "parameter name");
        param.put("type", "type of parameter, ie String, int, etc");

        ArrayNode params = objectMapper.createArrayNode();
        params.add(param);

        // "method" object
        ObjectNode method = objectMapper.createObjectNode();
        method.put("modifier", "public/protected/private");
        method.put("name", "name of method");
        method.set("params", params);
        method.put("body", "code");
        method.put("returnType", "void");
        method.set("throwsList", objectMapper.createArrayNode()); // empty array

        // "methods" array
        ArrayNode methods = objectMapper.createArrayNode();
        methods.add(method);

        // "parameters" object
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.set("imports", objectMapper.createArrayNode().add("required imports"));
        parameters.set("methods", methods);

        // "function" object
        ObjectNode function = objectMapper.createObjectNode();
        function.put("name", "insert_code");
        function.put("description", "Inserts Java code into the document for the user");
        function.set("parameters", parameters);

        // top-level object
        ObjectNode tool = objectMapper.createObjectNode();
        tool.put("type", "function");
        tool.set("function", function);


        return tool;
    }
    
    public InsertCodeToolCall parseResponse(String responseBody) {
        System.out.println("response " + responseBody);
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(responseBody, InsertCodeToolCall.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
