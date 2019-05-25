package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVMethod;
import com.github.e13mort.codeview.CVMethodParameter;
import com.github.e13mort.codeview.CVType;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class JavaParserMethodDescription implements CVMethod {

    @NotNull
    private final MethodDeclaration methodDeclaration;

    JavaParserMethodDescription(@NotNull MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    @NotNull
    @Override
    public String name() {
        return methodDeclaration.getName().asString();
    }

    @NotNull
    @Override
    public CVType returnType() {
        return new JavaParserMethodType(methodDeclaration);
    }

    @NotNull
    @Override
    public List<CVMethodParameter> parameters() {
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        ArrayList<CVMethodParameter> result = new ArrayList<>(parameters.size());
        for (Parameter parameter : parameters) {
            result.add(new JavaParserMethodParameter(parameter));
        }
        return result;
    }

}
