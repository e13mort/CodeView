package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVType;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.jetbrains.annotations.NotNull;

class JavaParserType implements CVType {
    @NotNull
    private final FieldDeclaration fieldDeclaration;

    JavaParserType(@NotNull FieldDeclaration fieldDeclaration) {
        this.fieldDeclaration = fieldDeclaration;
    }

    @NotNull
    @Override
    public String simpleName() {
        return fieldDeclaration.getVariables().get(0).getTypeAsString();
    }

    @NotNull
    @Override
    public String fullName() {
        return fieldDeclaration.getVariables().get(0).getTypeAsString();
    }
}
