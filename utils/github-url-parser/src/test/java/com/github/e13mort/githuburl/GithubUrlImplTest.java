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
    void testAllKindsParsing(Kind kind, String expected) {
        SourcesUrl.PathDescription description = githubUrl.parse(RETROFIT_URL);
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
                Arguments.of(Kind.BRANCH, "master"),
                Arguments.of(Kind.PATH, "retrofit/src/main/java/retrofit2"),
                Arguments.of(Kind.USER_NAME, "square"),
                Arguments.of(Kind.PROJECT_NAME, "retrofit"),
                Arguments.of(Kind.GIT_URL_HTTPS, "https://github.com/square/retrofit.git")
        );
    }

    private enum AssertVariant {
        NULL, NON_NULL;

        void performAssertion(Object o) {
            assertTrue((o != null && this == NON_NULL) || (o == null && this == NULL));
        }
    }
}