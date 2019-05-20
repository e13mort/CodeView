package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SampleCVClass implements CVClass {
    @NotNull
    @Override
    public String name() {
        return "SampleClass";
    }

    @NotNull
    @Override
    public List<CVClassField> fields() {
        ArrayList<CVClassField> cvClassFields = new ArrayList<>();
        cvClassFields.add(new CVClassField() {
            @NotNull
            @Override
            public String name() {
                return "sampleField";
            }

            @NotNull
            @Override
            public CVType type() {
                return new SampleCVType();
            }

            @NotNull
            @Override
            public CVVisibility visibilityModificator() {
                return CVVisibility.PRIVATE;
            }
        });
        return cvClassFields;
    }

    @NotNull
    @Override
    public List<CVMethod> methods() {
        ArrayList<CVMethod> cvMethods = new ArrayList<>();
        cvMethods.add(new CVMethod() {
            @NotNull
            @Override
            public String name() {
                return "sampleMethod";
            }

            @NotNull
            @Override
            public CVType returnType() {
                return new SampleCVType();
            }

            @NotNull
            @Override
            public List<CVMethodParameter> parameters() {
                return Collections.emptyList();
            }
        });
        return cvMethods;
    }

}
