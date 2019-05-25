package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVType;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.jetbrains.annotations.NotNull;

class JavaParserMethodType implements CVType {
    @NotNull
    private final MethodDeclaration methodDeclaration;

    JavaParserMethodType(@NotNull MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    @NotNull
    @Override
    public String simpleName() {
        return methodDeclaration.getType().asString();
    }

    @NotNull
    @Override
    public String fullName() {
        return methodDeclaration.getType().asString();
    }
}
