package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class ResolversPathDescriptionImpl implements GithubUrl.PathDescription {

    @NotNull
    private final Map<Kind, PathResolver> resolverMap;
    @NotNull
    private final String path;

    ResolversPathDescriptionImpl(@NotNull String path, @NotNull Map<Kind, PathResolver> resolverMap) {
        this.resolverMap = resolverMap;
        this.path = path;
    }

    @Nullable
    @Override
    public String readPart(@NotNull Kind kind) {
        return resolver(kind).resolve(path);
    }

    @Override
    public boolean hasPart(@NotNull Kind kind) {
        return resolver(kind).canResolve(path);
    }

    private PathResolver resolver(@NotNull Kind kind) {
        PathResolver resolver = resolverMap.get(kind);
        return resolver != null ? resolver : PathResolver.EMPTY;
    }
}
