package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVInterface;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jetbrains.annotations.NotNull;

public class JavaParserInterfaceDescription implements CVInterface {

    @NotNull
    private final ClassOrInterfaceType interfaceDeclaration;

    JavaParserInterfaceDescription(@NotNull ClassOrInterfaceType interfaceDeclaration) {
        this.interfaceDeclaration = interfaceDeclaration;
    }

    @NotNull
    @Override
    public String name() {
        return interfaceDeclaration.getName().asString();
    }
}
