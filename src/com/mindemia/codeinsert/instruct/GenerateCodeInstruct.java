/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.instruct;

import com.mindemia.codeinsert.AICompletionClient;
import com.mindemia.codeinsert.AICompletionOptionsPanel;
import com.mindemia.codeinsert.CodeContextExtractor;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

public class GenerateCodeInstruct implements CodeGenerator {

    
    private final int CONTEXT_LENGTH = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("context_length", 10);
    private final String SYSTEM_PROMPT = NbPreferences.forModule(AICompletionOptionsPanel.class).get("system_prompt_instruct", "");
    
    private final JTextComponent component;
    private final AiInstructClient aiClient = new AiInstructClient();

    private GenerateCodeInstruct(Lookup context) {
        component = context.lookup(JTextComponent.class);
    }

    @Override
    public String getDisplayName() {
        return "Generate code from instruction...";
    }

    @MimeRegistration(mimeType = "text/x-java", service = CodeGenerator.Factory.class, position = 1000)
    public static class Factory implements CodeGenerator.Factory {

        @Override
        public List<? extends CodeGenerator> create(Lookup context) {
            JTextComponent comp = context.lookup(JTextComponent.class);
            if (comp != null) {
                return Collections.singletonList(new GenerateCodeInstruct(context));
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public void invoke() {
        JTextArea textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);

        int option = JOptionPane.showConfirmDialog(null, scrollPane, "Instruction", JOptionPane.OK_CANCEL_OPTION);
        
        String instruction = textArea.getText();
        if (option == JOptionPane.CANCEL_OPTION || instruction == null || instruction.trim().isEmpty()) return;

        int caret = component.getCaretPosition();

        try {
            String prompt = CodeContextExtractor.constructInstructPrompt(component, caret, instruction, SYSTEM_PROMPT);
            new Thread(() -> {
                String result = aiClient.fetchSuggestion(prompt, "");
                if (result != null && !result.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            component.getDocument().insertString(caret, result, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


