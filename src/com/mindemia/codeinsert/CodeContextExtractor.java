/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

/**
 *
 * @author rickard
 */
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class CodeContextExtractor {
    
    final static String fimTemplate = """
                               <|fim_prefix|>
                               %s
                               <|fim_suffix|>
                               %s
                               <|fim_middle|>
                                """;
    final static String instructTemplate = """
                                    <|im_start|>system
                                    %s<|im_end|>
                                    <|im_start|>user
                                    %s<|im_end|>
                                    <|im_start|>assistant
                                    """;
    
    public static String constructFimPrompt(JTextComponent component, int maxLines, String userPrompt, String systemPrompt) {
        try {
            int caretPos = component.getCaretPosition();
            String fullText = component.getText(0, component.getDocument().getLength());

            String[] prefixSuffix = extractCode(fullText, caretPos, maxLines);

            return userPrompt + String.format(fimTemplate, prefixSuffix[0], prefixSuffix[1]);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "/* Error extracting context */";
        }
    }
    
    public static String constructInstructPrompt(JTextComponent component, int maxLines, String userPrompt, String systemPrompt) {
        try {
            int caretPos = component.getCaretPosition();
            String fullText = component.getText(0, component.getDocument().getLength());

            String[] prefixSuffix = extractCode(fullText, caretPos, maxLines);
            
            

            return String.format(instructTemplate, systemPrompt, userPrompt + "\n\n<code>" + prefixSuffix[0] + "/* Insert your code here */" + prefixSuffix[1] + "</code>");
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "/* Error extracting context */";
        }
    }
    
    public static String[] extractCode(String fullText, int caretPos, int maxLines ) {
        String[] lines = fullText.split("\n");

        int currentLineIndex = getLineIndex(fullText, caretPos);
        // Get the current line split at the caret
        String currentLine = lines[currentLineIndex];
        int column = caretPos - getLineStartOffset(fullText, currentLineIndex);
        String beforeCaret = currentLine.substring(0, column);
        String afterCaret = currentLine.substring(column);


        // Get previous lines
        int startLine = Math.max(0, currentLineIndex - maxLines);
        StringBuilder prefix = new StringBuilder();
        for (int i = startLine; i < currentLineIndex; i++) {
            prefix.append(lines[i]).append("\n");
        }
        prefix.append(beforeCaret);

        // Get next lines
        int endLine = Math.min(lines.length, currentLineIndex + maxLines + 1);
        StringBuilder suffix = new StringBuilder();
        suffix.append(afterCaret).append("\n");
        for (int i = currentLineIndex + 1; i < endLine; i++) {
            suffix.append(lines[i]).append("\n");
        }
        return new String[]{prefix.toString(), suffix.toString()};
    }

    private static int getLineIndex(String text, int offset) {
        return text.substring(0, offset).split("\n").length - 1;
    }

    private static int getLineStartOffset(String text, int lineIndex) {
        String[] lines = text.split("\n");
        int offset = 0;
        for (int i = 0; i < lineIndex; i++) {
            offset += lines[i].length() + 1; // +1 for newline
        }
        return offset;
    }
}