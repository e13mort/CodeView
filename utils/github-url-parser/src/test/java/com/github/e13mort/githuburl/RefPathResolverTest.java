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
            "https://github.com/user/someproject/tree/"
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
            "https://github.com/user/someproject/tree/master/path/file.dat"
    })
    @ParameterizedTest
    void validStringCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @CsvSource({"https://github.com/user/someproject/tree/master/path,master",
            "https://github.com/user/someproject/tree/branch1/path/subpath,branch1",
            "https://github.com/user/someproject/tree/someref/path/file.dat,someref"})
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