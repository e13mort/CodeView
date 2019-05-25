package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVType;
import com.github.javaparser.ast.type.Type;
import org.jetbrains.annotations.NotNull;

class JavaParserMethodParameterType implements CVType {

    @NotNull
    private final Type type;

    JavaParserMethodParameterType(@NotNull Type type) {
        this.type = type;
    }

    @NotNull
    @Override
    public String simpleName() {
        return type.asString();
    }

    @NotNull
    @Override
    public String fullName() {
        return type.asString();
    }
}
