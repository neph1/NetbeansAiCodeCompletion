package com.mindemia.codeinsert.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "AIChatTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "com.myplugin.AIChatTopComponentAction")
@ActionReference(path = "Menu/Window", position = 100)
@TopComponent.OpenActionRegistration(
        displayName = "AI Chat"
)
public class AIChatTopComponent extends TopComponent {

    private final JTextPane chatArea = new JTextPane();
    private final JTextArea inputArea = new JTextArea();
    private final AiChatClient aiChatClient = new AiChatClient();
    
    private final String lineBreak = "\n";
    
    private List<ChatMessage> chatHistory = new ArrayList<>();

    public AIChatTopComponent() {
        setName("AI Chat");
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setEditorKit(new HTMLEditorKit());
//        chatArea.setLineWrap(true);
//        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Optional: Pressing Enter inserts newline, Shift+Enter sends message
        inputArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        inputArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");

        inputArea.getActionMap().put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prompt = inputArea.getText();
                append("User: " + prompt);
                chatHistory.add(new ChatMessage(ChatMessage.Role.USER, prompt));
                callAI(inputArea.getText());
                inputArea.setText("");
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(inputScrollPane, BorderLayout.SOUTH);
    }

    private void append(String text) {
        StyledDocument document = (StyledDocument) chatArea.getDocument();
        try {
            document.insertString(document.getLength(), text + lineBreak, null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void callAI(String prompt) {
        new Thread(() -> {
            String response = aiChatClient.fetchSuggestion(prompt);
            SwingUtilities.invokeLater(() -> {
                try {
                    tryRenderMessage("AI: " + response);
                } catch (BadLocationException ex) {
                    append("AI: " + response);
                }
            });
            
            chatHistory.add(new ChatMessage(ChatMessage.Role.ASSISTANT, response));
        }).start();
    }

    private void tryRenderMessage(String text) throws BadLocationException {
        StyledDocument document = (StyledDocument) chatArea.getDocument();
        boolean inCodeBlock = false;
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                continue;
            }

            if (inCodeBlock) {
                SimpleAttributeSet codeAttr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(codeAttr, "Monospaced");
                StyleConstants.setBackground(codeAttr, new Color(240, 240, 240));
                document.insertString(document.getLength(), line + "\n", codeAttr);
            } else {
                document.insertString(document.getLength(), line + "\n", null);
            }
        }
    }

}
