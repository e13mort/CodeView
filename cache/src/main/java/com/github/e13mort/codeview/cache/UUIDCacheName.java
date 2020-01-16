package com.github.e13mort.codeview.cache;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDCacheName implements CacheName {

    @NotNull
    @Override
    public String createFileName() {
        return UUID.randomUUID().toString() + ".java";
    }

    @Override
    public String createDirName() {
        return "c" + UUID.randomUUID().toString().replace("-", "");
    }
}
