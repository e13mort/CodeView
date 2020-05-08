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

class GithubDelimiterPositionPathResolver implements PathResolver {
    private int minimumDelimiterCount;

    GithubDelimiterPositionPathResolver(int minimumDelimiterCount) {
        this.minimumDelimiterCount = minimumDelimiterCount;
    }

    @Nullable
    @Override
    public String resolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return null;
        int startIndex = findStartIndex(path);
        if (startIndex == -1) return null;
        int endIndex = path.indexOf(DELIMITER, startIndex + 1);
        if (endIndex == -1) endIndex = path.length();
        return path.substring(startIndex, endIndex);
    }

    @Override
    public boolean canResolve(@NotNull String path) {
        if (!path.startsWith(PART_HTTPS_GITHUB_COM)) return false;
        return findStartIndex(path) != -1;
    }

    private int findStartIndex(@NotNull String path) {
        int index, startIndex = 0, delimiterCounter = 0;
        while ((index = path.indexOf(DELIMITER, startIndex)) != -1 ) {
            startIndex = index + 1;
            if (++delimiterCounter >= minimumDelimiterCount && index != path.length() - 1)
                return startIndex;
        }
        return -1;
    }
}
