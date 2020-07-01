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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class GithubUrlImplTest {

    private static final String RETROFIT_URL = "https://github.com/square/retrofit/tree/master/retrofit/src/main/java/retrofit2";
    private static final String RETROFIT_BLOB_URL = "https://github.com/square/retrofit/blob/master/retrofit/src/main/java/retrofit2/some.file";

    private GithubUrlImpl githubUrl;

    @BeforeEach
    void setUp() {
        githubUrl = new GithubUrlImpl();
    }

    @ParameterizedTest
    @MethodSource("args")
    void urlParsing(String path, AssertVariant assertVariant) {
        assertVariant.performAssertion(githubUrl.parse(path));
    }

    @Test
    void testRetrofitParsed() {
        SourcesUrl.PathDescription description = githubUrl.parse(RETROFIT_URL);
        assertNotNull(description);
    }


    @ParameterizedTest
    @MethodSource("kinds")
    void testAllKindsParsing(String url, Kind kind, String expected) {
        SourcesUrl.PathDescription description = githubUrl.parse(url);
        assertEquals(expected, description.readPart(kind));
    }

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of("https://github.com", AssertVariant.NON_NULL),
                Arguments.of("invalidPath", AssertVariant.NULL)
        );
    }

    private static Stream<Arguments> kinds() {
        return Stream.of(
                Arguments.of(RETROFIT_URL, Kind.BRANCH, "master"),
                Arguments.of(RETROFIT_URL, Kind.PATH, "retrofit/src/main/java/retrofit2"),
                Arguments.of(RETROFIT_URL, Kind.USER_NAME, "square"),
                Arguments.of(RETROFIT_URL, Kind.PROJECT_NAME, "retrofit"),
                Arguments.of(RETROFIT_URL, Kind.GIT_URL_HTTPS, "https://github.com/square/retrofit.git"),
                Arguments.of(RETROFIT_URL, Kind.FILE_NAME, ""),
                Arguments.of(RETROFIT_BLOB_URL, Kind.BRANCH, "master"),
                Arguments.of(RETROFIT_BLOB_URL, Kind.PATH, "retrofit/src/main/java/retrofit2/some.file"),
                Arguments.of(RETROFIT_BLOB_URL, Kind.USER_NAME, "square"),
                Arguments.of(RETROFIT_BLOB_URL, Kind.PROJECT_NAME, "retrofit"),
                Arguments.of(RETROFIT_BLOB_URL, Kind.GIT_URL_HTTPS, "https://github.com/square/retrofit.git"),
                Arguments.of(RETROFIT_BLOB_URL, Kind.FILE_NAME, "some.file")
        );
    }

    private enum AssertVariant {
        NULL, NON_NULL;

        void performAssertion(Object o) {
            assertTrue((o != null && this == NON_NULL) || (o == null && this == NULL));
        }
    }
}