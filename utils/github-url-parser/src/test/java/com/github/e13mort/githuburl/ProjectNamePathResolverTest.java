package com.github.e13mort.githuburl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNamePathResolverTest {

    private ProjectNamePathResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ProjectNamePathResolver();
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
    })
    @ParameterizedTest
    void invalidStringCannotBeResolved(String path) {
        assertFalse(resolver.canResolve(path), path);
    }

    @ValueSource(strings = {
            "https://github.com/user/someproject",
            "https://github.com/user/someproject/",
            "https://github.com/user/someproject/tree",
    })
    @ParameterizedTest
    void validStringCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @CsvSource({"https://github.com/user/someproject,someproject",
            "https://github.com/user/someproject/,someproject"})
    @ParameterizedTest
    void validResolvedPath(String path, String expected) {
        assertEquals(expected, resolver.resolve(path));
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/user/",
            "https://github.com/user/"
    })
    @ParameterizedTest
    void validateNullPath(String path) {
        assertNull(resolver.resolve(path));
    }
}