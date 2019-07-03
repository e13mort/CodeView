package factory

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
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

    enum class OutputFormat {PUML, PNG}

    val sourcesPath: String by argument("sources", help = "Path to sources root").default(BuildConfig.DEFAULT_SOURCE_ROOT_PATH)

    val outputFileName: String by option("--out-name", help = "Output file name").default(BuildConfig.DEFAULT_OUTPUT_FILE_NAME)

    val outputFormat: OutputFormat by argument("format", help = "Output format")
        .choice(OutputFormat.PNG.name.toLowerCase() to OutputFormat.PNG, OutputFormat.PUML.name.toLowerCase() to OutputFormat.PUML)
        .default(OutputFormat.PNG)

    val githubKey by option()

}