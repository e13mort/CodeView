package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HttpsGitResolver implements PathResolver {

    private static final String GIT_EXTENSION = ".git";
    @NotNull
    private final ProjectNamePathResolver projectNamePathResolver;
    @NotNull
    private final UserNamePathResolver userNamePathResolver;

    HttpsGitResolver() {
        projectNamePathResolver = new ProjectNamePathResolver();
        userNamePathResolver = new UserNamePathResolver();
    }

    @Nullable
    @Override
    public String resolve(@NotNull String path) {
        String projectName = projectNamePathResolver.resolve(path);
        String userName = userNamePathResolver.resolve(path);
        if (projectName == null || userName == null) return null;
        return PART_HTTPS_GITHUB_COM + DELIMITER + userName + DELIMITER + projectName + GIT_EXTENSION;
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        return projectNamePathResolver.canResolve(path) && userNamePathResolver.canResolve(path);
    }
}
