package com.github.e13mort.codeview.backend.java;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class UUIDCacheName implements TemporarySourceSet.CacheName {
    @NotNull
    @Override
    public String createDirName() {
        return "tmp";
    }

    @NotNull
    @Override
    public String createFileName() {
        return UUID.randomUUID().toString() + ".java";
    }
}
