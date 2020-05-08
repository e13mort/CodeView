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
