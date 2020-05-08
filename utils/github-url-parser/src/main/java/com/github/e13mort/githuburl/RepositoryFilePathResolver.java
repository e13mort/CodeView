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
