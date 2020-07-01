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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FileNameResolverTest {

    private final PathResolver resolver = new FileNameResolver();

    @ValueSource(strings = {
            "https://github.com/some.file",
            "https://github.com/user/some.file.txt",
            "some.file.txt",
            "file.txt"
    })
    @ParameterizedTest
    void testValidPathsCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @ValueSource(strings = {
            "https://github.com/",
            "https://github.com/user/file",
            "file"
    })
    @ParameterizedTest
    void testInvalidPathsCanNotBeResolved(String path) {
        assertFalse(resolver.canResolve(path));
    }

    @CsvSource({
            "https://github.com/some.file,some.file",
            "https://github.com/user/some.file.txt,some.file.txt",
            "some.file.txt,some.file.txt",
            "file.txt,file.txt"
    })
    @ParameterizedTest
    void testResolvedNameIsValid(String path, String expectedFileName) {
        assertEquals(expectedFileName, resolver.resolve(path));
    }

    @ValueSource(strings = {
            "https://github.com/",
            "https://github.com/user/file",
            "file"
    })
    @ParameterizedTest
    void testInvalidNameResolvedNull(String path) {
        assertNull(resolver.resolve(path));
    }
}