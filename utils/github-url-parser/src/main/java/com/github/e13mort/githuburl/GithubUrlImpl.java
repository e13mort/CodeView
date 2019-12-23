package com.github.e13mort.githuburl;

import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GithubUrlImpl implements SourcesUrl {

    @Override
    public boolean canParse(@NotNull String path) {
        return isValidGithubPath(path);
    }

    @Nullable
    @Override
    public PathDescription parse(@NotNull String path) {
        if (isValidGithubPath(path)) return new ResolversPathDescriptionImpl(path, createResolvers());
        return null;
    }

    @NotNull
    private Map<Kind, PathResolver> createResolvers() {
        Map<Kind, PathResolver> map = new HashMap<>();
        map.put(Kind.PATH, new RepositoryFilePathResolver());
        map.put(Kind.BRANCH, new RefPathResolver());
        map.put(Kind.PROJECT_NAME, new ProjectNamePathResolver());
        map.put(Kind.USER_NAME, new UserNamePathResolver());
        return map;
    }

    private boolean isValidGithubPath(@NotNull String path) {
        return path.startsWith("https://github.com");
    }
}
