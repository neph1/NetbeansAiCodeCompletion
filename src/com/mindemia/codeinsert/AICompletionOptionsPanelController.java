/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Lookup;
import org.openide.util.HelpCtx;

import javax.swing.*;
import java.beans.PropertyChangeListener;

@OptionsPanelController.SubRegistration(
    location = "Advanced",
    displayName = "AI Code Completion",
    keywords = "AI, Code Completion",
    keywordsCategory = "Advanced/AICompletion"
)
public final class AICompletionOptionsPanelController extends OptionsPanelController {
    private AICompletionOptionsPanel panel;

    @Override
    public void update() {
        panel.loadSettings();
    }

    @Override
    public void applyChanges() {
        panel.saveSettings();
    }

    @Override
    public void cancel() {}

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isChanged() {
        return true;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JComponent getComponent(Lookup lookup) {
        if (panel == null) {
            panel = new AICompletionOptionsPanel();
        }
        return panel;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {}

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {}
}
