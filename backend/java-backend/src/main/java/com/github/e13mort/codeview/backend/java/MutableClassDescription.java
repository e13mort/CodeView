package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVClass;
import com.github.e13mort.codeview.CVClassField;
import com.github.e13mort.codeview.CVMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MutableClassDescription implements CVClass {

    private final String className;
    private ArrayList<CVClassField> cvClassFields = new ArrayList<>();
    private ArrayList<CVMethod> methods = new ArrayList<>();

    MutableClassDescription(@NotNull String className) {
        this.className = className;
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

    void addField(@NotNull CVClassField field) {
        this.cvClassFields.add(field);
    }

    void addMethod(@NotNull CVMethod method) {
        this.methods.add(method);
    }
}
