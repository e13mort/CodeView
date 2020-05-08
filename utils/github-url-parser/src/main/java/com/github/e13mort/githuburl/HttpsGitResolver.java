/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

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
