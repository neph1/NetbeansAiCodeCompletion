/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools;

import java.util.List;

/**
 *
 * @author rickard
 */
public record MethodSpec(String modifier, String name, List<ParameterSpec> params, String body){
}


