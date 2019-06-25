package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class RepositoryFilePathResolver implements PathResolver {

    @Nullable
    @Override
    public String resolve(@NotNull String path) {
        int refDelimiter = refDelimiter(path);
        return hasPathPart(path, refDelimiter) ? path.substring(refDelimiter + 1) : null;
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return false;
        int refDelimiter = refDelimiter(path);
        return hasPathPart(path, refDelimiter);
    }

    private boolean hasPathPart(@NotNull String path, int refDelimiter) {
        return refDelimiter != -1 && refDelimiter != path.length() - 1;
    }

    private int refDelimiter(@NotNull String path) {
        int treeDelimiter = path.indexOf(PART_TREE);
        if (treeDelimiter == -1) return -1;
        return path.indexOf('/', treeDelimiter + PART_TREE.length());
    }
}
