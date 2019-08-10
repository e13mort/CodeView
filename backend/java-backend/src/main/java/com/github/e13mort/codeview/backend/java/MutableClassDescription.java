package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.*;
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
    private ArrayList<CVInterface> implementedInterfaces = new ArrayList<>();
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

    @NotNull
    @Override
    public List<CVInterface> implemented() {
        return implementedInterfaces;
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

    void addImplementedInterface(@NotNull CVInterface implementedInterface) {
        this.implementedInterfaces.add(implementedInterface);
    }

}
