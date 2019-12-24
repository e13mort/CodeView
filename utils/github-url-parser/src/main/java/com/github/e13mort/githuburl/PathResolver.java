package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface PathResolver {
    String PART_TREE = "/tree/";
    String PART_HTTPS_GITHUB_COM = "https://github.com";
    char DELIMITER = '/';

    PathResolver EMPTY = new PathResolver() {
        @Nullable
        @Override
        public String resolve(@NotNull String path) {
            return null;
        }

        @Override
        public boolean canResolve(@NotNull String path) {
            return false;
        }
    };

    @Nullable
    String resolve(@NotNull String path);

    boolean canResolve(@NotNull String path);
}
