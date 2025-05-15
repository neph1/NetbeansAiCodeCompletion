/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools;

import java.util.Optional;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;

/**
 *
 * @author rickard
 */
public class EditorUtils {
    public static Optional<JTextComponent> getActiveTextComponent() {
        JTextComponent comp = EditorRegistry.lastFocusedComponent();
        return Optional.ofNullable(comp);
    }
}