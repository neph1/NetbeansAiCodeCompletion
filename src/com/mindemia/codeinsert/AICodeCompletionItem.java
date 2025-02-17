/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

/**
 *
 * @author rickard
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.*;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

public class AICodeCompletionItem implements CompletionItem {

    private final String text;
    private int caretOffset;
    private static Color fieldColor = Color.decode("0x0000B2");
    
    public AICodeCompletionItem(){
        text = "";
    }

    public AICodeCompletionItem(String text, int caretOffset) {
        this.text = text;
        this.caretOffset = caretOffset;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        try {
            final StyledDocument doc = (StyledDocument) component.getDocument();
            doc.insertString(component.getCaretPosition(), text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void processKeyEvent(KeyEvent evt) {
    }

    @Override
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(text, null, g, defaultFont);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(null, text, null, g, defaultFont,
            (selected ? Color.white : fieldColor), width, height, selected);
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 50;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }
}
