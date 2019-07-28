package com.github.e13mort.codeview.backend.java;

import org.jetbrains.annotations.NotNull;

interface CacheName {
    @NotNull
    String createDirName();

    @NotNull
    String createFileName();
}
