/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.chat;

import java.io.Serializable;

/**
 *
 * @author rickard
 */
public class ChatMessage implements Serializable {
    public enum Role { USER, ASSISTANT }

    private Role role;
    private String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public Role getRole() { return role; }
    public String getContent() { return content; }
}
