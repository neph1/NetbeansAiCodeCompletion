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
    public static String extractFimPrompt(JTextComponent component, int maxLines, String extraPrompt) {
        try {
            int caretPos = component.getCaretPosition();
            String fullText = component.getText(0, component.getDocument().getLength());
            String[] lines = fullText.split("\n");

            int currentLineIndex = getLineIndex(fullText, caretPos);
            
            // Get previous lines
            int startLine = Math.max(0, currentLineIndex - maxLines);
            StringBuilder prefix = new StringBuilder();
            for (int i = startLine; i < currentLineIndex; i++) {
                prefix.append(lines[i]).append("\n");
            }

            // Get the current line split at the caret
            String currentLine = lines[currentLineIndex];
            int column = caretPos - getLineStartOffset(fullText, currentLineIndex);
            String beforeCaret = currentLine.substring(0, column);
            String afterCaret = currentLine.substring(column);

            // Get next lines
            int endLine = Math.min(lines.length, currentLineIndex + maxLines + 1);
            StringBuilder suffix = new StringBuilder();
            for (int i = currentLineIndex + 1; i < endLine; i++) {
                suffix.append(lines[i]).append("\n");
            }

            // Construct FIM prompt
            return extraPrompt
                    + "<|fim_prefix|>\n" + prefix +
                   beforeCaret + "\n" +
                   "<|fim_suffix|>\n" + afterCaret + "\n" + suffix + "\n" +
                    "<|fim_middle|>";
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "/* Error extracting context */";
        }
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