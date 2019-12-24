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
