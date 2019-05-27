package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVClass;
import com.github.e13mort.codeview.CVClassField;
import com.github.e13mort.codeview.CVMethod;
import com.github.e13mort.codeview.ClassProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MutableClassDescription implements CVClass {

    @NotNull
    private final String className;
    @NotNull
    private ArrayList<CVClassField> cvClassFields = new ArrayList<>();
    @NotNull
    private ArrayList<CVMethod> methods = new ArrayList<>();
    @NotNull
    private List<ClassProperty> properties;

    MutableClassDescription(@NotNull String className, @NotNull List<ClassProperty> properties) {
        this.className = className;
        this.properties = properties;
    }

    @NotNull
    @Override
    public String name() {
        return className;
    }

    @NotNull
    @Override
    public List<CVClassField> fields() {
        return cvClassFields;
    }

    @NotNull
    @Override
    public List<CVMethod> methods() {
        return methods;
    }

    @Override
    public boolean has(@NotNull ClassProperty property) {
        return properties.contains(property);
    }

    void addField(@NotNull CVClassField field) {
        this.cvClassFields.add(field);
    }

    void addMethod(@NotNull CVMethod method) {
        this.methods.add(method);
    }
}
