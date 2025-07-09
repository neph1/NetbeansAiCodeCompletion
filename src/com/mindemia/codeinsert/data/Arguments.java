/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.data;

import java.util.List;

/**
 *
 * @author rickard
 */
public record Arguments(List<String> imports, List<Method> methods, String patch) {

    public Arguments() {
        this(List.of(), List.of(), "");
    }

    public Arguments(List<Method> methods) {
        this(List.of(), methods, "");
    }
    
    public Arguments(String patch) {
        this(List.of(), List.of(), patch);
    }
}
