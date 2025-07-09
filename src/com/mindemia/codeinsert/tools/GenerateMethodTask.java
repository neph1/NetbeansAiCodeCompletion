/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.tools;

import com.mindemia.codeinsert.data.Method;
import com.mindemia.codeinsert.data.Parameter;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author rickard
 */
public class GenerateMethodTask {

    public static void insertGeneratedMethods(JTextComponent textComponent, List<Method> methods) throws IOException {
        JavaSource javaSource = JavaSource.forDocument(textComponent.getDocument());

        if (javaSource == null) {
            System.err.println("No JavaSource found for this document.");
            return;
        }

        javaSource.runModificationTask((WorkingCopy wc) -> {
            wc.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);

            TreePath path = wc.getTreeUtilities().pathFor(textComponent.getCaretPosition());
            TreePath classPath = findClassPath(path);

            if (classPath == null) {
                System.err.println("No class found at caret.");
                return;
            }

            ClassTree classTree = (ClassTree) classPath.getLeaf();
            TreeMaker make = wc.getTreeMaker();
            GeneratorUtilities gu = GeneratorUtilities.get(wc);

            for (Method spec : methods) {
                Set<Modifier> modifiers = parseModifiers(spec.modifier());
                ModifiersTree modTree = make.Modifiers(modifiers);

                List<VariableTree> params = new ArrayList<>();
                if (spec.params() != null) {
                    for (Parameter param : spec.params()) {
                        params.add(make.Variable(make.Modifiers(Collections.emptySet()), param.name(), make.Identifier(param.type()), null));
                    }
                }

                MethodTree method = make.Method(
                        modTree,
                        spec.name(),
                        make.PrimitiveType(TypeKind.VOID),
                        Collections.emptyList(), // type params
                        params,
                        Collections.emptyList(), // throws
                        "{ " + spec.body() + " }",
                        null
                );

                classTree = make.addClassMember(classTree, gu.importFQNs(method));
            }

            wc.rewrite(classPath.getLeaf(), classTree);
        }).commit();
    }

    private static TreePath findClassPath(TreePath path) {
        while (path != null) {
            if (path.getLeaf() instanceof ClassTree) {
                return path;
            }
            path = path.getParentPath();
        }
        return null;
    }

    private static Set<Modifier> parseModifiers(String text) {
        if (text == null) return Collections.emptySet();
        switch (text.toLowerCase()) {
            case "public": return EnumSet.of(Modifier.PUBLIC);
            case "protected": return EnumSet.of(Modifier.PROTECTED);
            case "private": return EnumSet.of(Modifier.PRIVATE);
            default: return Collections.emptySet();
        }
    }

}
