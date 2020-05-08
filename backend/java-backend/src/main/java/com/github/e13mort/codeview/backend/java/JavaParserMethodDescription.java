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
