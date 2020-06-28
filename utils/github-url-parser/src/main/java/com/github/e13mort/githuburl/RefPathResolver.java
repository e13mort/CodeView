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

class RefPathResolver implements PathResolver {
    @Nullable
    @Override
    public String resolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return null;
        int refStart = refStart(path);
        if (refStart == -1) return null;
        int refEnd = path.indexOf('/', refStart);
        if (refEnd == -1) refEnd = path.length() - 1;
        return path.substring(refStart, refEnd);
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return false;
        return refStart(path) != -1;
    }

    private int refStart(@NotNull String path) {
        int treePart = path.indexOf(PART_TREE);
        if (treePart == -1) treePart = path.indexOf(PART_BLOB);
        if (treePart == -1) return -1;
        int refPart = treePart + PART_TREE.length();
        return refPart != path.length() ? refPart : -1;
    }
}
