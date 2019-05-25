package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVMethodParameter;
import com.github.e13mort.codeview.CVType;
import com.github.javaparser.ast.body.Parameter;
import org.jetbrains.annotations.NotNull;

class JavaParserMethodParameter implements CVMethodParameter {
    @NotNull
    private final Parameter parameter;

    JavaParserMethodParameter(@NotNull Parameter parameter) {
        this.parameter = parameter;
    }

    @NotNull
    @Override
    public String name() {
        return parameter.getNameAsString();
    }

    @NotNull
    @Override
    public CVType type() {
        return new JavaParserMethodParameterType(parameter.getType());
    }
}
