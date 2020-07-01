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
        map.put(Kind.GIT_URL_HTTPS, new HttpsGitResolver());
        map.put(Kind.FILE_NAME, new FileNameResolver());
        return map;
    }

    private boolean isValidGithubPath(@NotNull String path) {
        return path.startsWith("https://github.com");
    }
}
