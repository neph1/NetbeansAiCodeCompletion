/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.completion;

import com.mindemia.codeinsert.AICompletionOptionsPanel;
import com.mindemia.codeinsert.CodeContextExtractor;
import static com.mindemia.codeinsert.CodeContextExtractor.extractCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;

/**
 *
 * @author rickard
 */
@ActionID(
    category = "Edit",
    id = "com.mindemia.codeinsert.completion.TriggerAISuggestionAction"
)
@ActionRegistration(
    displayName = "Trigger AI Suggestion"
)
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "DS-A")
})
public final class TriggerAISuggestionAction implements ActionListener {
    
    private final int CONTEXT_LENGTH = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("context_length", 10);

    public TriggerAISuggestionAction() {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent textComponent = EditorRegistry.focusedComponent();
        if (textComponent == null) {
            return;
        }
        if (!textComponent.isVisible() || textComponent.getCaret() == null) {
            System.out.println("Component is not valid " + textComponent.getName() + " " + textComponent.getCaret());
            return;
        }
        int caretOffset = textComponent.getCaretPosition();
        
        String[] prefixSuffix = extractCode(textComponent.getText(), caretOffset, CONTEXT_LENGTH);

        String suggestion = new AiFimClient().fetchSuggestion(prefixSuffix[0], null, prefixSuffix[1]);
        suggestion = AICodeCompletionProvider.trimRepeatedPrefix(prefixSuffix[0], suggestion);

        AIPopupPanel.showSuggestionPopup(textComponent, caretOffset, suggestion);
    }
}
