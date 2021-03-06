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
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaBackend implements CVTransformation<CVTransformation.TransformOperation<Path>, CVClasses> {

    @NotNull
    @Override
    public Single<TransformOperation<CVClasses>> prepare(@NotNull CVTransformation.TransformOperation<Path> transformation) {
        return Single.fromCallable(() -> new TransformOperation<CVClasses>() {
            @NotNull
            @Override
            public OperationState state() {
                return transformation.state();
            }

            @NotNull
            @Override
            public Single<CVClasses> transform() {
                return transformation
                        .transform()
                        .map(JavaBackend.this::performTransformation);
            }

            @NotNull
            @Override
            public String description() {
                return transformation.description();
            }
        });
    }

    @NotNull
    private CVClasses performTransformation(@NotNull Path path) {
        MutableCVClasses result = new MutableCVClasses();
        ProjectRoot root = new SymbolSolverCollectionStrategy().collect(path);
        SourceRoot sourceRoot = new SourceRoot(root.getRoot());
        try {
            List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();
            parseResults.forEach(compilationUnitParseResult -> {
                CVClass classDescription = createCodeViewClassEntity(compilationUnitParseResult);
                if (classDescription == null) return;
                result.add(classDescription);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Nullable
    private CVClass createCodeViewClassEntity(@NotNull ParseResult<CompilationUnit> compilationUnitParseResult) {
        Optional<CompilationUnit> optional = compilationUnitParseResult.getResult();
        if (!optional.isPresent()) return null;
        CompilationUnit unit = optional.get();
        NodeList<TypeDeclaration<?>> types = unit.getTypes();
        if (types.isEmpty()) return null;
        TypeDeclaration<?> declaration = types.get(0);
        String className = declaration.getNameAsString();

        List<ClassProperty> properties = new ArrayList<>();
        if (declaration instanceof ClassOrInterfaceDeclaration
                && ((ClassOrInterfaceDeclaration) declaration).isInterface()) {
            properties.add(ClassProperty.INTERFACE);
        }

        MutableClassDescription classDescription = new MutableClassDescription(className, properties);

        unit.accept(new MutableClassDescriptionVisitor(), classDescription);
        return classDescription;
    }
}
