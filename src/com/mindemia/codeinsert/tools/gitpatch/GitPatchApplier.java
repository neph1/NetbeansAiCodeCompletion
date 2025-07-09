/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools.gitpatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

public class GitPatchApplier {

    public static void applyPatch(File projectRoot, GitPatch gitPatch) throws IOException, BadLocationException {
        File targetFile = new File(projectRoot, gitPatch.relativePath());
        FileObject fileObject = FileUtil.toFileObject(FileUtil.normalizeFile(targetFile));
        if (fileObject == null || !fileObject.isValid()) {
            return; // File doesn't exist, skip
        }

        DataObject dataObject = DataObject.find(fileObject);
        EditorCookie editor = dataObject.getLookup().lookup(EditorCookie.class);
        if (editor == null) return;

        Document doc = editor.openDocument();
        if (doc instanceof StyledDocument styledDoc) {
            NbDocument.runAtomicAsUser(styledDoc, () -> {
                try {
                    applyToDocument(styledDoc, gitPatch);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void applyToDocument(Document doc, GitPatch patch) throws BadLocationException {
        List<String> lines = Arrays.asList(doc.getText(0, doc.getLength()).split("\n", -1));
        List<String> newLines = new ArrayList<>(lines);

        // Apply hunks in reverse order to avoid shifting lines
        List<GitPatchHunk> hunks = new ArrayList<>(patch.hunks());
        Collections.reverse(hunks);

        for (GitPatchHunk hunk : hunks) {
            int startLine = hunk.startLineOld() - 1;
            int endLine = startLine + hunk.oldLines().size();

            // Remove old lines
            for (int i = endLine - 1; i >= startLine; i--) {
                int offset = getLineStartOffset(doc, i);
                int len = getLineLength(doc, i);
                doc.remove(offset, len);
            }

            // Insert new lines
            int insertOffset = getLineStartOffset(doc, startLine);
            StringBuilder insertText = new StringBuilder();
            for (String newLine : hunk.newLines()) {
                insertText.append(newLine).append("\n");
            }
            doc.insertString(insertOffset, insertText.toString(), null);
        }
    }

    private static int getLineStartOffset(Document doc, int line) throws BadLocationException {
        Element root = doc.getDefaultRootElement();
        line = Math.max(0, Math.min(root.getElementCount() - 1, line));
        return root.getElement(line).getStartOffset();
    }

    private static int getLineLength(Document doc, int line) throws BadLocationException {
        Element root = doc.getDefaultRootElement();
        line = Math.max(0, Math.min(root.getElementCount() - 1, line));
        Element element = root.getElement(line);
        return element.getEndOffset() - element.getStartOffset();
    }
}
