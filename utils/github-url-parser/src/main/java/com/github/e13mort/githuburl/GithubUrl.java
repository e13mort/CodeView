package com.github.e13mort.githuburl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GithubUrl {

    @Nullable
    PathDescription parse();

    boolean canParse(@NotNull String path);

    interface PathDescription {
        enum Kind {
            PROJECT_NAME, USER_NAME, BRANCH, PATH
        }

        String EMPTY_PART = "";

        @NotNull
        String readPart(@NotNull Kind kind);

        boolean hasPart(@NotNull Kind... kind);
    }
}
