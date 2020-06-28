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
import static org.junit.jupiter.api.Assertions.assertNull;

class RefPathResolverTest {

    private RefPathResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new RefPathResolver();
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
            "https://github.com/user/someproject/",
            "https://github.com/user/someproject/tree/",
            "https://github.com/user/someproject/blob/"
    })
    @ParameterizedTest
    void invalidStringCannotBeResolved(String path) {
        assertFalse(resolver.canResolve(path), path);
    }

    @ValueSource(strings = {
            "https://github.com/user/someproject/tree/master",
            "https://github.com/user/someproject/tree/master/",
            "https://github.com/user/someproject/tree/master/path",
            "https://github.com/user/someproject/tree/master/path/subpath",
            "https://github.com/user/someproject/blob/master/path/file.dat"
    })
    @ParameterizedTest
    void validStringCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @CsvSource({"https://github.com/user/someproject/tree/master/path,master",
            "https://github.com/user/someproject/tree/branch1/path/subpath,branch1",
            "https://github.com/user/someproject/blob/someref/path/file.dat,someref"})
    @ParameterizedTest
    void validResolvedPath(String path, String expected) {
        assertEquals(expected, resolver.resolve(path));
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
            "https://github.com/user/someproject/",
            "https://github.com/user/someproject/tree/"
    })
    @ParameterizedTest
    void validateNullPath(String path) {
        assertNull(resolver.resolve(path));
    }
}