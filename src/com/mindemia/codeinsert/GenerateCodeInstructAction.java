/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rickard
 */
@ActionID(
    category = "Edit",
    id = "com.myplugin.GenerateCodeAction"
)
@ActionRegistration(
    displayName = "Generate Code from Instruction"
)
@ActionReference(
    path = "Shortcuts", name = "DS-I" // Ctrl+Shift+I
)
public final class GenerateCodeInstructAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent comp = EditorRegistry.focusedComponent();
        if (comp == null) return;

        Lookup context = Lookups.fixed(comp);
        List<? extends CodeGenerator> generators = new GenerateCodeInstruct.Factory().create(context);
        if (!generators.isEmpty()) {
            generators.get(0).invoke();
        }
    }
}
