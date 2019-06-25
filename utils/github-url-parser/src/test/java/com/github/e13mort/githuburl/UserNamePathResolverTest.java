package com.github.e13mort.githuburl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserNamePathResolverTest {

    private UserNamePathResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new UserNamePathResolver();
    }

    @ValueSource(strings = {
            "https://github.com",
            "https://github.com/",
            "invalid",
    })
    @ParameterizedTest
    void invalidStringCannotBeResolved(String path) {
        assertFalse(resolver.canResolve(path), path);
    }

    @ValueSource(strings = {
            "https://github.com/user",
            "https://github.com/user/",
            "https://github.com/user/someproject",
    })
    @ParameterizedTest
    void validStringCanBeResolved(String path) {
        assertTrue(resolver.canResolve(path));
    }

    @CsvSource({"https://github.com/user,user",
            "https://github.com/user/,user",
            "https://github.com/user/project,user"
    })
    @ParameterizedTest
    void validResolvedPath(String path, String expected) {
        assertEquals(expected, resolver.resolve(path));
    }

    @ValueSource(strings = {
            "https://github.com",
            "invalid",
            "https://github.com/"
    })
    @ParameterizedTest
    void validateNullPath(String path) {
        assertNull(resolver.resolve(path));
    }
}