package com.github.e13mort.githuburl;

import com.github.e13mort.githuburl.GithubUrl.PathDescription.Kind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ResolversPathDescriptionImplTest {

    private static final String TEST_USER = "testUser";

    @ParameterizedTest
    @EnumSource(Kind.class)
    void allKindsAreInvalidForEmptyResolvers(Kind kind) {
        ResolversPathDescriptionImpl pathDescription = new ResolversPathDescriptionImpl("github.com", Collections.emptyMap());
        assertEquals(GithubUrl.PathDescription.EMPTY_PART, pathDescription.readPart(kind));
        assertFalse(pathDescription.hasPart(kind));
    }

    @ParameterizedTest
    @EnumSource(Kind.class)
    void positiveResolverCalled(Kind kind) {
        ResolversPathDescriptionImpl pathDescription = new ResolversPathDescriptionImpl("github.com", Collections.singletonMap(kind, new PositivePathResolver()));
        assertTrue(pathDescription.hasPart(kind));
        assertEquals(TEST_USER, pathDescription.readPart(kind));
    }

    private static class PositivePathResolver implements PathResolver {
        @Nullable
        @Override
        public String resolve(@NotNull String path) {
            return TEST_USER;
        }

        @Override
        public boolean canResolve(@NotNull String path) {
            return true;
        }
    }
}