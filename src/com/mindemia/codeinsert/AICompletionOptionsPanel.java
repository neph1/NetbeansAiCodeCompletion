/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import org.openide.util.NbPreferences;

public class AICompletionOptionsPanel extends JPanel {

    private JTextField apiKeyField;

    // FIM
    private JTextField hostFimField;
    private JTextField modelFimField;
    private JTextField systemPromptFimField;

    // Chat
    private JTextField hostChatField;
    private JTextField modelChatField;
    private JTextField systemPromptChatField;

    // Instruct
    private JTextField hostInstructField;
    private JTextField modelInstructField;
    private JTextField systemPromptInstructField;

    // Common
    private JTextField maxTokensField;
    private JTextField contextLengthField;

    public AICompletionOptionsPanel() {
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        setLayout(new GridLayout(20, 1, 5, 5));

        // General
        add(new JLabel("🔐 API Key:"));
        apiKeyField = new JTextField(30);
        add(apiKeyField);

        // FIM Section
        add(new JLabel("📌 FIM Settings:"));
        add(labeled("Host FIM:", hostFimField = new JTextField(50)));
        add(labeled("Model FIM:", modelFimField = new JTextField(30)));
        add(labeled("System Prompt FIM:", systemPromptFimField = new JTextField(100)));

        // Chat Section
        add(new JLabel("💬 Chat Settings:"));
        add(labeled("Host Chat:", hostChatField = new JTextField(50)));
        add(labeled("Model Chat:", modelChatField = new JTextField(30)));
        add(labeled("System Prompt Chat:", systemPromptChatField = new JTextField(100)));

        // Instruct Section
        add(new JLabel("🛠 Instruct Settings:"));
        add(labeled("Host Instruct:", hostInstructField = new JTextField(50)));
        add(labeled("Model Instruct:", modelInstructField = new JTextField(30)));
        add(labeled("System Prompt Instruct:", systemPromptInstructField = new JTextField(100)));

        // Common Settings
        add(new JLabel("⚙️ Shared Settings:"));
        add(labeled("Max Tokens:", maxTokensField = new JTextField(10)));
        add(labeled("Context Length (lines):", contextLengthField = new JTextField(10)));
    }

    private JPanel labeled(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(labelText), BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    protected void loadSettings() {
        Preferences prefs = NbPreferences.forModule(AICompletionOptionsPanel.class);

        apiKeyField.setText(prefs.get("api_key", ""));

        hostFimField.setText(prefs.get("host_fim", "http://127.0.0.1:8080/v1/completions"));
        modelFimField.setText(prefs.get("model_fim", ""));
        systemPromptFimField.setText(prefs.get("system_prompt_fim", ""));

        hostChatField.setText(prefs.get("host_chat", "http://127.0.0.1:8080/v1/completions"));
        modelChatField.setText(prefs.get("model_chat", ""));
        systemPromptChatField.setText(prefs.get("system_prompt_chat", "You are Qwen, created by Alibaba Cloud. You are a helpful assistant."));

        hostInstructField.setText(prefs.get("host_instruct", "http://127.0.0.1:8080/v1/completions"));
        modelInstructField.setText(prefs.get("model_instruct", ""));
        systemPromptInstructField.setText(prefs.get("system_prompt_instruct", "You are Qwen, created by Alibaba Cloud. You are a helpful assistant. Respond only with the generated code without repeating anything the user says. Do not repeat any code supplied by the user."));
        
        maxTokensField.setText(String.valueOf(prefs.getInt("max_tokens", 300)));
        contextLengthField.setText(String.valueOf(prefs.getInt("context_length", 10)));
    }

    protected void saveSettings() {
        Preferences prefs = NbPreferences.forModule(AICompletionOptionsPanel.class);

        prefs.put("api_key", apiKeyField.getText());

        prefs.put("host_fim", hostFimField.getText());
        prefs.put("model_fim", modelFimField.getText());
        prefs.put("system_prompt_fim", systemPromptFimField.getText());

        prefs.put("host_chat", hostChatField.getText());
        prefs.put("model_chat", modelChatField.getText());
        prefs.put("system_prompt_chat", systemPromptChatField.getText());

        prefs.put("host_instruct", hostInstructField.getText());
        prefs.put("model_instruct", modelInstructField.getText());
        prefs.put("system_prompt_instruct", systemPromptInstructField.getText());

        prefs.putInt("max_tokens", Integer.parseInt(maxTokensField.getText()));
        prefs.putInt("context_length", Integer.parseInt(contextLengthField.getText()));
    }
}


