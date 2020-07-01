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

public class FileNameResolver implements PathResolver {
    @Nullable
    @Override
    public String resolve(@NotNull String inputPath) {
        final int lastDot = inputPath.lastIndexOf('.');
        boolean mayBeFile = lastDot >= 0;
        if (!mayBeFile) return null;
        final int lastSlash = inputPath.lastIndexOf('/');
        boolean mayBeDirectory = lastSlash >= 0;
        if (!mayBeDirectory) return inputPath;
        if (lastDot < lastSlash) return null;
        return inputPath.substring(lastSlash + 1);
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        final int lastDot = path.lastIndexOf('.');
        boolean mayBeFile = lastDot >= 0;
        if (!mayBeFile) return false;
        final int lastSlash = path.lastIndexOf('/');
        return lastDot > lastSlash;
    }
}
