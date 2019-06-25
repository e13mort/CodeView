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