package com.github.e13mort.githuburl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GithubUrlImplTest {

    @ParameterizedTest
    @MethodSource("args")
    void urlParsing(String path, AssertVariant assertVariant) {
        GithubUrlImpl githubUrl = new GithubUrlImpl();
        assertVariant.performAssertion(githubUrl.parse(path));
    }

    @Disabled
    @Test
    void canParse() {
        fail("Implement");
    }

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of("https://github.com", AssertVariant.NON_NULL),
                Arguments.of("invalidPath", AssertVariant.NULL)
        );
    }

    private enum AssertVariant {
        NULL, NON_NULL;

        void performAssertion(Object o) {
            assertTrue((o != null && this == NON_NULL) || (o == null && this == NULL));
        }
    }
}