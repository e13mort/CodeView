package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.Backend;
import com.github.e13mort.codeview.CVClass;
import com.github.e13mort.codeview.ClassProperty;
import com.github.e13mort.codeview.SourceFile;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JavaBackend implements Backend {

    @NotNull
    private TemporarySourceSet temporarySourceSet = new TemporarySourceSet(new UUIDCacheName());

    @NotNull
    @Override
    public List<CVClass> transformSourcesToCVClasses(@NotNull List<? extends SourceFile> files) {
        try (TemporarySourceSet.TemporarySources sources = temporarySourceSet.cacheFiles(files)) {
            Path path = sources.files();
            return performTransformation(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @NotNull
    private List<CVClass> performTransformation(@NotNull Path path) {
        List<CVClass> result = new ArrayList<>();
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
        TypeDeclaration<?> declaration = unit.getTypes().get(0);
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
