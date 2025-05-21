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
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        Set<String> imports = new HashSet<>();
        String currentPackage = getCurrentPackageAndImports(currentSource, imports);

        currentSource.runUserActionTask(controller -> {
            controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);

            CompilationUnitTree cut = controller.getCompilationUnit();
            Elements elements = controller.getElements();
            
            // This is for classes in same package 

            PackageElement pkg = elements.getPackageElement(currentPackage);
            for (Element e : pkg.getEnclosedElements()) {
                // GOTCHA: Comparing class names
                if ((!currentFile.getName().equals(e.getSimpleName().toString()) && (e.getKind().isClass()) || e.getKind().isInterface())) {
                    FileObject fileObject = SourceUtils.getFile(ElementHandle.create((TypeElement) e), controller.getClasspathInfo());
                    if (fileObject != null) {
                        contextSnippets.addAll(getContents(getDocument(fileObject), currentPackage, imports));

                    }
                }
            }
            
            // This is for imported classes

            var imports2 = cut.getImports();

            for (ImportTree importTree : imports2) {
                String fqn = importTree.getQualifiedIdentifier().toString();

                TypeElement type = elements.getTypeElement(fqn);
                if (type != null) {
                    ElementHandle<TypeElement> handle = ElementHandle.create(type);

                    for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
                        FileObject fileObject = curRoot.getFileObject(controller.getClasspathInfo().toString());
                        if (fileObject != null) {
                            contextSnippets.addAll(getContents(getDocument(fileObject), currentPackage, imports));
                        }
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

    private static String getCurrentPackageAndImports(JavaSource source, Set<String> importsOut) {
        final String[] pkgHolder = new String[1];

        try {
            source.runUserActionTask(controller -> {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                CompilationUnitTree cu = controller.getCompilationUnit();
                if (cu.getPackageName() != null) {
                    pkgHolder[0] = cu.getPackageName().toString();
                }

                cu.getImports().forEach(imp -> {
                    importsOut.add(imp.getQualifiedIdentifier().toString());
                });

            }, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pkgHolder[0] != null ? pkgHolder[0] : "";
    }

    private static List<String> getContents(Document document, String currentPackage, Set<String> imports) throws IOException {
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

            String filePackage = cu.getPackageName() != null ? cu.getPackageName().toString() : "";

            boolean samePackage = filePackage.equals(currentPackage);
            boolean explicitlyImported = cu.getImports().stream()
                    .anyMatch(imp -> imports.contains(imp.getQualifiedIdentifier().toString()));

            if (samePackage || explicitlyImported) {
                contextSnippets.addAll(extractContent(cu));
            }
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
                    } else if (member instanceof VariableTree vt) {
                        builder.append("  ")
                                .append(vt.getModifiers()).append(" ")
                                .append(vt.getType()).append(" ")
                                .append(vt.getName()).append(";\n");
                    }
                }
                builder.append("}\n");
                contextSnippets.add(builder.toString());
            }
        }
        return contextSnippets;
    }
}
