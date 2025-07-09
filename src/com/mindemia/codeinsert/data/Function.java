/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author rickard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Function(String name, String description, Arguments arguments) {

}
