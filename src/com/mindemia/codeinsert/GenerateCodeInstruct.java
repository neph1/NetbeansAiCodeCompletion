/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Lookup;
import com.sun.source.util.TreePath;

public class GenerateCodeInstruct implements CodeGenerator {

    final TreePath path;
    private final JTextComponent component;

    private GenerateCodeInstruct(Lookup context) {
        path = context.lookup(TreePath.class);
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
        String instruction = JOptionPane.showInputDialog("Instruction:");
        if (instruction == null || instruction.trim().isEmpty()) return;

        int caret = component.getCaretPosition();

        try {
            String text = component.getText(0, component.getDocument().getLength());
            String prefix = text.substring(0, caret);
            String suffix = text.substring(caret);

            String fimPrompt = instruction + "\n"
                    + "<|fim_prefix|>\n" + prefix + "\n" +
                   "<|fim_suffix|>\n" + suffix + "\n" +
                    "<|fim_middle|>";

            // Async call to AI
            new Thread(() -> {
                String result = new AICompletionClient().fetchSuggestion(fimPrompt);

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


