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

package factory

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.e13mort.codeview.cli.BuildConfig

class LaunchCommand : NoRunCliktCommand(
    printHelpOnEmptyArgs = true,
    name = BuildConfig.APP_NAME
) {

    init {
        context {
            autoEnvvarPrefix = BuildConfig.APP_NAME.toUpperCase()
        }
    }

    enum class OutputFormat { PUML, PNG }

    enum class GithubClient { REST, GIT }

    val sourcesPath: String by argument(
        "sources",
        help = "Path to sources root"
    ).default(BuildConfig.DEFAULT_SOURCE_ROOT_PATH)

    val outputFileName: String? by option(
        "--out-name",
        help = "Output file name"
    )

    val outputFormat: OutputFormat by argument("format", help = "Output format")
        .choice(
            OutputFormat.PNG.name.toLowerCase() to OutputFormat.PNG,
            OutputFormat.PUML.name.toLowerCase() to OutputFormat.PUML
        )
        .default(OutputFormat.PNG)

    val githubKey by option()

    val githubClient: GithubClient by option("--github-client", help = "Github client type")
        .choice(
            GithubClient.GIT.name.toLowerCase() to GithubClient.GIT,
            GithubClient.REST.name.toLowerCase() to GithubClient.REST
        )
        .default(GithubClient.GIT)

    val verbose by option("-v", "--verbose", help = "Verbose mode").flag()

}