/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

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
