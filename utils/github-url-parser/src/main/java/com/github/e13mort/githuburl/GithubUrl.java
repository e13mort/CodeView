package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GithubUrl {

    @Nullable
    PathDescription parse();

    interface PathDescription {
        enum Kind {
            PROJECT_NAME, USER_NAME, BRANCH, PATH
        }

        @Nullable
        String readPart(@NotNull Kind kind);

        boolean hasPart(@NotNull Kind kind);
    }
}
