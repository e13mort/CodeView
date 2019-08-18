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
    private ArrayList<CVClass> implementedInterfaces = new ArrayList<>();
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

    @Override
    public boolean has(@NotNull ClassProperty property) {
        return properties.contains(property);
    }

    @Override
    public void accept(@NotNull MethodsVisitor methodsVisitor) {
        for (CVMethod method : methods) {
            methodsVisitor.onMethodDetected(this, method);
        }
    }

    @Override
    public void accept(@NotNull FieldsVisitor fieldsVisitor) {
        for (CVClassField cvClassField : cvClassFields) {
            fieldsVisitor.onFieldDetected(this, cvClassField);
        }
    }

    @Override
    public void accept(@NotNull RelationVisitor relationVisitor) {
        for (CVClass implementedInterface : implementedInterfaces) {
            relationVisitor.onImplementedInterfaceDetected(this, implementedInterface);
        }
    }

    void addField(@NotNull CVClassField field) {
        this.cvClassFields.add(field);
    }

    void addMethod(@NotNull CVMethod method) {
        this.methods.add(method);
    }

    void addImplementedInterface(@NotNull CVClass implementedInterface) {
        this.implementedInterfaces.add(implementedInterface);
    }
}
