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

import com.github.e13mort.codeview.ClassProperty;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Collections;

class MutableClassDescriptionVisitor extends VoidVisitorAdapter<MutableClassDescription> {
    @Override
    public void visit(MethodDeclaration n, MutableClassDescription arg) {
        arg.addMethod(new JavaParserMethodDescription(n.clone()));
    }

    @Override
    public void visit(FieldDeclaration n, MutableClassDescription arg) {
        arg.addField(new JavaParserFieldDescription(n.clone()));
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, MutableClassDescription arg) {
        super.visit(n, arg);
        n.getImplementedTypes().forEach(interfaceDeclaration ->
            arg.addImplementedInterface(
                    new MutableClassDescription(
                            interfaceDeclaration.getName().asString(), Collections.singletonList(ClassProperty.INTERFACE)
                    )
            )
        );
    }
}
