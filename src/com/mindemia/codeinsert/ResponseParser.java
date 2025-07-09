/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindemia.codeinsert.data.AIResponse;
import com.mindemia.codeinsert.data.Choice;
import com.mindemia.codeinsert.data.InsertCodeToolCall;
import com.mindemia.codeinsert.data.Patch;
import com.mindemia.codeinsert.tools.EditorUtils;
import com.mindemia.codeinsert.tools.GenerateMethodTask;
import com.mindemia.codeinsert.tools.gitpatch.GitPatch;
import com.mindemia.codeinsert.tools.gitpatch.GitPatchApplier;
import com.mindemia.codeinsert.tools.gitpatch.PatchParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author rickard
 */
public class ResponseParser {

    private final ObjectMapper objectMapper;

    public ResponseParser() {
        this.objectMapper = new ObjectMapper();

    }

    public AIResponse createResponse(String responseBody) throws JsonProcessingException {
        return objectMapper.readValue(responseBody, AIResponse.class);
    }

    public String parseResponse(String responseBody) {
        System.out.println("response " + responseBody);
        if (responseBody == null || responseBody.isEmpty()) {
            return "";
        }
        try {
            AIResponse aiResponse = createResponse(responseBody);
            if (aiResponse.choices().isEmpty()) {
                System.out.println("choices are empty ");
                return "/* AI response empty */";
            }
            Choice choice = aiResponse.choices().get(0);
            if (choice.message() != null) {
                System.out.println("message " + choice.message());
                if (choice.message().tool_calls() != null) {
                    for (InsertCodeToolCall tool : choice.message().tool_calls()) {
                        if (tool.function().name().equals("insert_code")) {
                            System.out.println("function " + tool.function().name());
                            var imports = tool.function().arguments().imports();
                            var methods = tool.function().arguments().methods();
                            try {
                                System.out.println("methods " + methods.size());
                                GenerateMethodTask.insertGeneratedMethods(EditorUtils.getActiveTextComponent().get(), methods);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            continue;
                        }
                        if (tool.function().name().equals("git_patch")) {
                            System.out.println("function " + tool.function().name());
                            String code = tool.function().arguments().patch();
                            List<File> projectDirs  = getProjects();
                            List<GitPatch> gitPatches = PatchParser.parse(code);
                            for(GitPatch gitPatch : gitPatches) {
                                for(File file: projectDirs) {
                                    try {
                                        GitPatchApplier.applyPatch(file, gitPatch);
                                    } catch (IOException | BadLocationException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            }
                        }
                    }
                }
                if (choice.message().content() != null) {
                    return choice.message().content().strip();
                }
            }
            if (choice.text() != null) {
                return choice.text().strip();
            }

            return "/* AI response empty */";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "/* Error parsing AI response */";
        }
    }

    public List<File> getProjects() {
        List<File> projects = new ArrayList<>();
        Node[] nodes = TopComponent.getRegistry().getActivatedNodes();
        for (Node node : nodes) {
            Project project = node.getLookup().lookup(Project.class);
            if (project == null) {
                FileObject file = node.getLookup().lookup(FileObject.class);
                if (file != null) {
                    project = FileOwnerQuery.getOwner(file);
                }
            }
            if (project != null) {
                
                projects.add(FileUtil.toFile(project.getProjectDirectory()));
            }
        }
        return projects;
    }
}
