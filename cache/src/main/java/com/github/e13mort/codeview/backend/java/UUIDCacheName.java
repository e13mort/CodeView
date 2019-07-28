package com.github.e13mort.codeview.backend.java;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDCacheName implements CacheName {
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
