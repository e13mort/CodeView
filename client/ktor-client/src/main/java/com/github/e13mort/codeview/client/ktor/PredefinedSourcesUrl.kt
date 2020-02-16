package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind

class PredefinedSourcesUrl : SourcesUrl {
    override fun parse(path: String): SourcesUrl.PathDescription? = PredefinedPathDescription()

    override fun canParse(path: String): Boolean = true

    private class PredefinedPathDescription : SourcesUrl.PathDescription {

        private val map = mapOf(
            Kind.GIT_URL_HTTPS to "https://github.com/e13mort/stf-console-client.git",
            Kind.BRANCH to "master",
            Kind.USER_NAME to "e13mort",
            Kind.PATH to "client/src/main/java/com/github/e13mort/stf/console/commands",
            Kind.PROJECT_NAME to "stf-console-client"
        )

        override fun readPart(kind: Kind): String {
            return map.getOrDefault(kind, "invalid")
        }

        override fun hasPart(vararg kind: Kind?): Boolean {
            return map.containsKey(kind[0])
        }

    }
}