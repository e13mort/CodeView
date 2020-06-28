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

interface PathResolver {
    String PART_TREE = "/tree/";
    String PART_BLOB = "/blob/";
    String PART_HTTPS_GITHUB_COM = "https://github.com";
    char DELIMITER = '/';

    PathResolver EMPTY = new PathResolver() {
        @Nullable
        @Override
        public String resolve(@NotNull String path) {
            return null;
        }

        @Override
        public boolean canResolve(@NotNull String path) {
            return false;
        }
    };

    @Nullable
    String resolve(@NotNull String path);

    boolean canResolve(@NotNull String path);
}
