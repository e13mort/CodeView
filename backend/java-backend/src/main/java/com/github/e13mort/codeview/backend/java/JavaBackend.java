package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVClass;
import com.github.e13mort.codeview.SourceFile;
import com.github.e13mort.codeview.Backend;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class JavaBackend implements Backend {

    public static void main(String[] args) throws IOException {

        ProjectRoot root = new SymbolSolverCollectionStrategy().collect(Paths.get("/Users/pavel/work/pets/MyApplication/backends/Java/src/main/java/com/github/e13mort/codeview/backend/java/"));

        SourceRoot sourceRoot = new SourceRoot(root.getRoot());
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();
        parseResults.forEach(new Consumer<ParseResult<CompilationUnit>>() {
            @Override
            public void accept(ParseResult<CompilationUnit> compilationUnitParseResult) {
                CompilationUnit compilationUnit = compilationUnitParseResult.getResult().get();

                VoidVisitor<Void> prettyPrintVisitor = new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(MethodDeclaration n, Void arg) {
                        printMethod(n);
                    }
                };
                compilationUnit.accept(prettyPrintVisitor, null);
//                System.out.println(prettyPrintVisitor.toString());
            }
        });
    }

    private static void printMethod(MethodDeclaration n) {
        System.out.println(convertMethodToString(n));
    }

    private static String convertMethodToString(MethodDeclaration n) {
        return n.getTypeAsString();
    }

    @NotNull
    @Override
    public List<CVClass> transformSourcesToCVClasses(@NotNull List<? extends SourceFile> files) {
        return Collections.singletonList(new SampleCVClass());
    }

}

//MethodDeclaration.getTypeAsString() - method type
//class name
//fields
//methods
