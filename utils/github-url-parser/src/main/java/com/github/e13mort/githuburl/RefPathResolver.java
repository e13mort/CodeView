package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class RefPathResolver implements PathResolver {
    @Nullable
    @Override
    public String resolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return null;
        int refStart = refStart(path);
        if (refStart == -1) return null;
        int refEnd = path.indexOf('/', refStart);
        if (refEnd == -1) refEnd = path.length() - 1;
        return path.substring(refStart, refEnd);
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return false;
        return refStart(path) != -1;
    }

    private int refStart(@NotNull String path) {
        int treePart = path.indexOf(PART_TREE);
        if (treePart == -1) return -1;
        int refPart = treePart + PART_TREE.length();
        return refPart != path.length() ? refPart : -1;
    }
}
