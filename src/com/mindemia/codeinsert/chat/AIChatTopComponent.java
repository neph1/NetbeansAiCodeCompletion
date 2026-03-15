package com.mindemia.codeinsert.chat;

import com.mindemia.codeinsert.tools.EditorUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Pair;
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

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JButton addChatButton = new JButton("Add Chat");
    private final AiChatClient aiChatClient;

    private Map<String, JTextComponent> openTabs;
    private JList<String> openTabsList;
    private DefaultListModel<String> listModel;

    private final Color backgroundColor = UIManager.getColor("EditorPane.background");
    private final Color foregroundColor = UIManager.getColor("EditorPane.foreground");

    private final String userPrompt = "User: ";
    private final String aiPrompt = "Assistant: ";

    private Map<String, List<ChatMessage>> chatHistory = new HashMap<>();

    public AIChatTopComponent() {
        setName("AI Chat");
        setLayout(new BorderLayout());

        addChatButton.addActionListener(e -> addNewChatArea());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addChatButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        openTabsList = new JList<>(listModel);
        openTabsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane contextFilesSelector = new JScrollPane(openTabsList);
        contextFilesSelector.setName("Extra context");
        contextFilesSelector.setPreferredSize(new Dimension(200, 80));

        topPanel.add(contextFilesSelector, BorderLayout.SOUTH);

        JButton refreshButton = new JButton("Refresh files");
        refreshButton.addActionListener((ActionEvent e) -> {
            refreshOpenTabsList();
        });
        topPanel.add(refreshButton, BorderLayout.WEST);

        aiChatClient = new AiChatClient();

    }

    private void addNewChatArea() {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagePanel);

        JTextArea inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        inputArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");

        inputArea.getActionMap().put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prompt = inputArea.getText().trim();
                if (prompt.isEmpty()) {
                    return;
                }

                addMessageComponent(messagePanel, userPrompt, prompt, false);
                var list = chatHistory.getOrDefault(tabbedPane.getSelectedComponent().getName(), new ArrayList<>());
                list.add(new ChatMessage(ChatMessage.Role.USER, prompt));
                chatHistory.put(tabbedPane.getSelectedComponent().getName(), list);

                callAI(messagePanel, prompt);
                inputArea.setText("");

                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
            }
        });

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputScrollPane, BorderLayout.SOUTH);

        tabbedPane.addTab("Chat " + (tabbedPane.getTabCount() + 1), chatPanel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, createTabComponent(tabbedPane, chatPanel));

//        insertTestMessage();
    }

    private JPanel createTabComponent(JTabbedPane tabbedPane, JPanel chatPanel) {
        JPanel tabComponent = new JPanel(new BorderLayout());
        tabComponent.add(new JLabel("Chat " + (tabbedPane.getTabCount() + 1)), BorderLayout.CENTER);

        JButton closeButton = new JButton("x");
        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfComponent(chatPanel);
            if (index != -1) {
                tabbedPane.remove(index);
            }
        });

        tabComponent.add(closeButton, BorderLayout.EAST);
        return tabComponent;
    }

    private void callAI(JPanel messagePanel, String prompt) {
        String selectedTab = tabbedPane.getSelectedComponent().getName();
        new Thread(() -> {
            String response = aiChatClient.fetchSuggestion(selectedTab, prompt, getSelectedTabs());
            SwingUtilities.invokeLater(() -> {
                addMessageComponent(messagePanel, aiPrompt, response, true);
                var list = chatHistory.getOrDefault(selectedTab, new ArrayList<>());
                list.add(new ChatMessage(ChatMessage.Role.ASSISTANT, response));
                chatHistory.put(tabbedPane.getSelectedComponent().getName(), list);
            });
        }).start();
    }

    private void addMessageComponent(JPanel container, String prefix, String content, boolean parseCodeBlocks) {
        if (!parseCodeBlocks) {
            JLabel label = new JLabel("<html><b>" + prefix + "</b> " + content.replace("\n", "<br>") + "</html>");
            label.setOpaque(true);
            label.setBackground(backgroundColor.darker());
            label.setForeground(foregroundColor);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            container.add(label);
            container.revalidate();
            return;
        }

        boolean inCodeBlock = false;
        StringBuilder codeBuffer = new StringBuilder();

        if (!prefix.isEmpty()) {
            JLabel label = new JLabel("<html><b>" + prefix + "</b></html>");
            label.setOpaque(true);
            label.setBackground(backgroundColor.darker());
            label.setForeground(foregroundColor);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            container.add(label);
        }

        for (String line : content.split("\n")) {
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                if (!inCodeBlock && codeBuffer.length() > 0) {
                    addCodeBlock(container, codeBuffer.toString());
                    codeBuffer.setLength(0);
                }
                continue;
            }

            if (inCodeBlock) {
                codeBuffer.append(line).append("\n");
            } else {
                JLabel label = new JLabel(line);
                label.setOpaque(true);
                label.setBackground(backgroundColor.darker());
                label.setForeground(foregroundColor);
                label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                container.add(label);
            }
        }

        if (codeBuffer.length() > 0) {
            addCodeBlock(container, codeBuffer.toString());
        }

        container.revalidate();
        container.repaint();
    }

    private void addCodeBlock(JPanel container, String codeText) {
        JTextArea codeArea = new JTextArea(codeText);
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.setEditable(false);
        codeArea.setBackground(backgroundColor.brighter());
        codeArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setMaximumSize(new Dimension(getWidth(), 500));
        scrollPane.setPreferredSize(new Dimension(150, scrollPane.getPreferredSize().height));

        JButton copyButton = new JButton("Copy");
        copyButton.setMaximumSize(new Dimension(200, 100));
        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(codeArea.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        });

        JPanel codePanel = new JPanel(new BorderLayout(5, 5));
        codePanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        codePanel.add(scrollPane, BorderLayout.CENTER);
        codePanel.add(copyButton, BorderLayout.SOUTH);
        codePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 700));

        container.add(codePanel);
    }

    private void refreshOpenTabsList() {
        listModel.clear();

        this.openTabs = getContextTabs();
        Set<String> tabNames = openTabs.keySet();
        for (String tab : tabNames) {
            listModel.addElement(tab);
        }
    }

    private Map<String, JTextComponent> getContextTabs() {
        List<? extends JTextComponent> tabs = EditorUtils.getOpenTextComponents();
        Map<String, JTextComponent> tabNames = new HashMap<>();
        for (JTextComponent tab : tabs) {
            
            Document doc = tab.getDocument();
            
            DataObject dataObject = NbEditorUtilities.getDataObject(doc);
            tabNames.put(dataObject.getPrimaryFile().getName(), tab);
        }
        return tabNames;
    }

    private Map<String, JTextComponent> getSelectedTabs() {
        Map<String, JTextComponent> selectedTabs = new HashMap<>();
        for (String s : openTabsList.getSelectedValuesList()) {
            selectedTabs.put(s, openTabs.get(s));
        }
        return selectedTabs;
    }

}
