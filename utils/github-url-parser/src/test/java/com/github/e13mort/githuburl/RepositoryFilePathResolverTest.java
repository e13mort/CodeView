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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryFilePathResolverTest {

    private RepositoryFilePathResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new RepositoryFilePathResolver();
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
            "https://github.com/user/someproject/",
            "https://github.com/user/someproject/tree/",
            "https://github.com/user/someproject/tree/master",
            "https://github.com/user/someproject/tree/master/"
    })
    @ParameterizedTest
    void invalidStringCannotBeResolved(String path) {
        assertFalse(resolver.canResolve(path), path);
    }

    @ValueSource(strings = {
            "https://github.com/user/someproject/tree/master/path",
            "https://github.com/user/someproject/tree/master/path/subpath",
            "https://github.com/user/someproject/tree/master/path/file.dat"
    })
    @ParameterizedTest
    void validStringCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @CsvSource({"https://github.com/user/someproject/tree/master/path,path",
            "https://github.com/user/someproject/tree/master/path/subpath,path/subpath",
            "https://github.com/user/someproject/tree/master/path/file.dat,path/file.dat"})
    @ParameterizedTest
    void validResolvedPath(String path, String expected) {
        assertEquals(expected, resolver.resolve(path));
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
            "https://github.com/user/someproject/",
            "https://github.com/user/someproject/tree/",
            "https://github.com/user/someproject/tree/master",
            "https://github.com/user/someproject/tree/master/"
    })
    @ParameterizedTest
    void validateNullPath(String path) {
        assertNull(resolver.resolve(path));
    }
}