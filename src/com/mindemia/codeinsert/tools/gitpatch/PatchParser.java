/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools.gitpatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rickard
 */
public class PatchParser {

    public static List<GitPatch> parse(String diffText) {
        List<GitPatch> result = new ArrayList<>();
        String[] lines = diffText.split("\\n");
        GitPatch currentPatch = null;
        List<String> hunkOld = null;
        List<String> hunkNew = null;
        int oldStart = 0, newStart = 0;

        for (String line : lines) {
            if (line.startsWith("--- ") || line.startsWith("--git")) {
                continue;
            } else if (line.startsWith("+++ ")) {
                String filePath = line.substring(4).trim().replaceFirst("b/", "");
                currentPatch = new GitPatch(filePath, new ArrayList<>());
                result.add(currentPatch);
            } else if (line.startsWith("@@")) {
                Matcher matcher = Pattern.compile("@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@").matcher(line);
                if (matcher.find()) {
                    oldStart = Integer.parseInt(matcher.group(1));
                    newStart = Integer.parseInt(matcher.group(2));
                    hunkOld = new ArrayList<>();
                    hunkNew = new ArrayList<>();
                    currentPatch.hunks().add(new GitPatchHunk(oldStart, hunkOld, newStart, hunkNew));
                }
            } else if (line.startsWith("+")) {
                hunkNew.add(line.substring(1));
            } else if (line.startsWith("-")) {
                hunkOld.add(line.substring(1));
            } else if (line.startsWith(" ")) {
                String context = line.substring(1);
                hunkOld.add(context);
                hunkNew.add(context);
            }
        }

        return result;
    }
}
