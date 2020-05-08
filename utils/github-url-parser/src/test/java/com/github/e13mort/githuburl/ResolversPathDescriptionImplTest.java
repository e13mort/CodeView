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

import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind;
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
        assertEquals(SourcesUrl.PathDescription.EMPTY_PART, pathDescription.readPart(kind));
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