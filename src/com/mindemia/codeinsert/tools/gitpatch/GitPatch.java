/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools.gitpatch;

import java.util.List;

/**
 *
 * @author rickard
 */
public record GitPatch(String relativePath, List<GitPatchHunk> hunks) {}
