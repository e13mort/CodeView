package com.github.e13mort.codeview.cache;

import io.reactivex.annotations.NonNull;
import org.jetbrains.annotations.NotNull;

public interface CacheName {

    @NotNull
    String createFileName();

    @NonNull
    String createDirName();
}
