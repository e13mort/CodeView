package com.github.e13mort.githuburl;

import com.github.e13mort.githuburl.GithubUrl.PathDescription.Kind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GithubUrlImpl implements GithubUrl {

    @NotNull
    private final String path;

    public GithubUrlImpl(@NotNull String path) {
        this.path = path;
    }

    @Nullable
    @Override
    public PathDescription parse() {
        return isValidGithubPath(path) ? new ResolversPathDescriptionImpl(path, createResolvers()) : null;
    }

    @Override
    public boolean canParse(@NotNull String path) {
        return isValidGithubPath(path);
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

    @Override
    public String toString() {
        return "GithubUrlImpl{" +
                "path='" + path + '\'' +
                '}';
    }
}
