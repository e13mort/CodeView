package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

class ResolversPathDescriptionImpl implements SourcesUrl.PathDescription {

    @NotNull
    private final Map<Kind, PathResolver> resolverMap;
    @NotNull
    private final String path;

    ResolversPathDescriptionImpl(@NotNull String path, @NotNull Map<Kind, PathResolver> resolverMap) {
        this.resolverMap = resolverMap;
        this.path = path;
    }

    @NotNull
    @Override
    public String readPart(@NotNull Kind kind) {
        String resolve = resolver(kind).resolve(path);
        return resolve != null ? resolve : EMPTY_PART;
    }

    @Override
    public boolean hasPart(@NotNull Kind... kinds) {
        for (Kind kind : kinds) {
            if (!resolver(kind).canResolve(path)) return false;
        }
        return true;
    }

    private PathResolver resolver(@NotNull Kind kind) {
        PathResolver resolver = resolverMap.get(kind);
        return resolver != null ? resolver : PathResolver.EMPTY;
    }

    @Override
    public String toString() {
        return "ResolversPathDescriptionImpl{" +
                "path='" + path + '\'' +
                '}';
    }
}
