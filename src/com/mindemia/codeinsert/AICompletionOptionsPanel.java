/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import org.openide.util.NbPreferences;

public class AICompletionOptionsPanel extends JPanel {

    private JTextField apiKeyField;
    private JTextField hostField;
    private JTextField maxTokensField;
    private JTextField modelField;
    private JTextField contextLengthField;

    public AICompletionOptionsPanel() {
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        JLabel apiKeyLabel = new JLabel("API Key:");
        apiKeyField = new JTextField(30);

        JLabel hostKeyLabel = new JLabel("Host:");
        hostField = new JTextField(50);

        JLabel maxTokensLabel = new JLabel("Max Tokens:");
        maxTokensField = new JTextField(10);

        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField(20);

        JLabel contextLengthLabel = new JLabel("Context Length (lines):");
        contextLengthField = new JTextField(10);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSettings());

        setLayout(new GridLayout(12, 1, 5, 5));
        add(apiKeyLabel);
        add(apiKeyField);
        add(hostKeyLabel);
        add(hostField);
        add(maxTokensLabel);
        add(maxTokensField);
        add(modelLabel);
        add(modelField);
        add(contextLengthLabel);
        add(contextLengthField);
        add(new JLabel());
        add(saveButton);
    }

    protected void loadSettings() {
        String apiKey = NbPreferences.forModule(AICompletionOptionsPanel.class).get("api_key", "");
        apiKeyField.setText(apiKey);

        String hostKey = NbPreferences.forModule(AICompletionOptionsPanel.class).get("host", "http://127.0.0.1:8080/v1/completions");
        hostField.setText(hostKey);

        int maxTokens = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("max_tokens", 300);
        maxTokensField.setText(String.valueOf(maxTokens));

        String model = NbPreferences.forModule(AICompletionOptionsPanel.class).get("model", "gpt-4");
        modelField.setText(model);

        int contextLength = NbPreferences.forModule(AICompletionOptionsPanel.class).getInt("context_length", 10);
        contextLengthField.setText(String.valueOf(contextLength));
    }

    protected void saveSettings() {
        NbPreferences.forModule(AICompletionOptionsPanel.class).put("api_key", apiKeyField.getText());
        NbPreferences.forModule(AICompletionOptionsPanel.class).put("host", hostField.getText());
        NbPreferences.forModule(AICompletionOptionsPanel.class).putInt("max_tokens", Integer.parseInt(maxTokensField.getText()));
        NbPreferences.forModule(AICompletionOptionsPanel.class).put("model", modelField.getText());
        NbPreferences.forModule(AICompletionOptionsPanel.class).putInt("context_length", Integer.parseInt(contextLengthField.getText()));
    }
}

