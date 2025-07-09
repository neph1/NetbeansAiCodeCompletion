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
public record Method(String modifier, String name, List<Parameter> params, String body, String returnType, List<String> throwsList) {

}
