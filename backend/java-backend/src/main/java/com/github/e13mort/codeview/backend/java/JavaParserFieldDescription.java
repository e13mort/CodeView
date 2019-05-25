package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVClassField;
import com.github.e13mort.codeview.CVType;
import com.github.e13mort.codeview.CVVisibility;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.jetbrains.annotations.NotNull;

class JavaParserFieldDescription implements CVClassField {

    @NotNull
    private final FieldDeclaration fieldDeclaration;

    JavaParserFieldDescription(@NotNull FieldDeclaration fieldDeclaration) {
        this.fieldDeclaration = fieldDeclaration;
    }

    @NotNull
    @Override
    public String name() {
        return fieldDeclaration.getVariables().get(0).getNameAsString();
    }

    @NotNull
    @Override
    public CVType type() {
        return new JavaParserType(fieldDeclaration);
    }

    @NotNull
    @Override
    public CVVisibility visibilityModificator() {
        return CVVisibility.PUBLIC;
    }

}
