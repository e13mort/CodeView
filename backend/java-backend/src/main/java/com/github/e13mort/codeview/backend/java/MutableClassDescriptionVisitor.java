package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.ClassProperty;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Collections;

class MutableClassDescriptionVisitor extends VoidVisitorAdapter<MutableClassDescription> {
    @Override
    public void visit(MethodDeclaration n, MutableClassDescription arg) {
        arg.addMethod(new JavaParserMethodDescription(n.clone()));
    }

    @Override
    public void visit(FieldDeclaration n, MutableClassDescription arg) {
        arg.addField(new JavaParserFieldDescription(n.clone()));
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, MutableClassDescription arg) {
        n.getImplementedTypes().forEach(interfaceDeclaration ->
            arg.addImplementedInterface(
                    new MutableClassDescription(
                            interfaceDeclaration.getName().asString(), Collections.singletonList(ClassProperty.INTERFACE)
                    )
            )
        );
    }
}
