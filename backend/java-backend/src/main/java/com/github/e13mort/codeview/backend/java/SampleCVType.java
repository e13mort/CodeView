package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CVType;
import org.jetbrains.annotations.NotNull;

class SampleCVType implements CVType {
    @NotNull
    @Override
    public String simpleName() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    @Override
    public String fullName() {
        return this.getClass().getCanonicalName();
    }
}
