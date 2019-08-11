package com.github.e13mort.codeview.cache;

import org.jetbrains.annotations.NotNull;

public interface CacheName {

    @NotNull
    String createFileName();
}
