/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert;

/**
 *
 * @author rickard
 */
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.source.*;
import org.netbeans.modules.editor.*;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

public class OpenFileContextCollector {

    public static List<String> collectContextFromOpenFiles(JTextComponent currentComponent) throws IOException {
        List<String> contextSnippets = new ArrayList<>();

        JavaSource currentSource = JavaSource.forDocument(currentComponent.getDocument());

        if (currentSource == null) {
            return contextSnippets;
        }
        FileObject currentFile = NbEditorUtilities.getFileObject(currentComponent.getDocument());

        currentSource.runUserActionTask(controller -> {
            controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
            

            CompilationUnitTree cut = controller.getCompilationUnit();
            String currentPackage = cut.getPackageName().toString();
            Elements elements = controller.getElements();
            
            var imports2 = cut.getImports();
            // Classes in same package 

            PackageElement pkg = elements.getPackageElement(currentPackage);
            for (Element e : pkg.getEnclosedElements()) {
                // GOTCHA: Comparing class names
                if ((!currentFile.getName().equals(e.getSimpleName().toString()) && (e.getKind().isClass()) || e.getKind().isInterface())) {
                    FileObject fileObject = SourceUtils.getFile(ElementHandle.create((TypeElement) e), controller.getClasspathInfo());
                    if (fileObject != null) {
                        contextSnippets.addAll(getContents(getDocument(fileObject)));

                    }
                }
            }
            
            // Imported classes


            for (ImportTree importTree : imports2) {
                String fqn = importTree.getQualifiedIdentifier().toString();
                TypeElement type = elements.getTypeElement(fqn);
                if (type != null) {
                    FileObject fileObject = SourceUtils.getFile(ElementHandle.create(type), controller.getClasspathInfo());
                    
                    if (fileObject == null) {
                        for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
                            fileObject = curRoot.getFileObject(controller.getClasspathInfo().toString());
                            break;
                        }
                    }
                    
                    if (fileObject != null) {
                        contextSnippets.addAll(getContents(getDocument(fileObject)));
                    }
                }
            }
        }, true);

        return contextSnippets;
    }
    
    private static Document getDocument(FileObject fileObject) throws DataObjectNotFoundException, IOException {
        DataObject dob = DataObject.find(fileObject);
        EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
        if (ec != null) {
            return ec.openDocument();
        }
        return null;
    }

    private static List<String> getContents(Document document) throws IOException {
        List<String> contextSnippets = new ArrayList<>();
        JavaSource javaSource = JavaSource.forDocument(document);
        if (javaSource == null) {
            return contextSnippets;
        }

        javaSource.runUserActionTask(controller -> {
            controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
            CompilationUnitTree cu = controller.getCompilationUnit();
            if (cu == null) {
                return;
            }


            contextSnippets.addAll(extractContent(cu));

        }, true);
        return contextSnippets;
    }

    private static List<String> extractContent(CompilationUnitTree cu) {
        List<String> contextSnippets = new ArrayList<>();
        for (Tree typeDecl : cu.getTypeDecls()) {
            if (typeDecl instanceof ClassTree ct) {
                StringBuilder builder = new StringBuilder("class " + ct.getSimpleName() + " {\n");

                for (Tree member : ct.getMembers()) {
                    if (member instanceof MethodTree mt) {
                        builder.append("  ")
                                .append(mt.getModifiers()).append(" ")
                                .append(mt.getReturnType()).append(" ")
                                .append(mt.getName()).append("(");
                        builder.append(String.join(", ",
                                mt.getParameters().stream()
                                        .map(p -> p.getType().toString() + " " + p.getName())
                                        .toList()));
                        builder.append(");\n");
                    }
                }
                builder.append("}\n");
                contextSnippets.add(builder.toString());
            }
        }
        return contextSnippets;
    }
}
