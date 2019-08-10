package com.github.e13mort.codeview.backend.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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
            arg.addImplementedInterface(new JavaParserInterfaceDescription(interfaceDeclaration))
        );
    }
}
