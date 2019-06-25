import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import com.github.e13mort.codeview.cli.BuildConfig
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.githuburl.GithubUrl
import com.github.e13mort.githuburl.GithubUrl.PathDescription.*
import com.github.e13mort.githuburl.GithubUrlImpl
import java.lang.IllegalArgumentException

fun main(params: Array<String>) {
    val factory = CliClientFactory()
    factory.main(params)
    factory.createCodeView().run()
}

class CliClientFactory : NoRunCliktCommand(
    printHelpOnEmptyArgs = true,
    name = BuildConfig.APP_NAME
) {

    init {
        context {
            autoEnvvarPrefix = BuildConfig.APP_NAME.toUpperCase()
        }
    }

    private enum class OutputFormat {PUML, PNG}

    private val sourcesPath: String by argument("sources", help = "Path to sources root").default(BuildConfig.DEFAULT_SOURCE_ROOT_PATH)

    private val outputFileName: String by option("--out-name", help = "Output file name").default(BuildConfig.DEFAULT_OUTPUT_FILE_NAME)

    private val outputFormat: OutputFormat by argument("format", help = "Output format")
        .choice(OutputFormat.PNG.name.toLowerCase() to OutputFormat.PNG, OutputFormat.PUML.name.toLowerCase() to OutputFormat.PUML)
        .default(OutputFormat.PNG)

    private val githubKey by option()

    fun createCodeView(): CodeView {
        return CodeView(
            createDataSource(sourcesPath),
            createFrontend(),
            createBackend(),
            createOutput(outputFileName, outputFormat)
        )
    }

    private fun createBackend() = JavaBackend()

    private fun createFrontend() = PulmFrontend()

    private fun createOutput(outputFileName: String, outputFormat: OutputFormat): Output = when (outputFormat) {
        OutputFormat.PUML -> SimpleFileOutput(outputFileName)
        OutputFormat.PNG -> PNGPumlFileOutput(outputFileName)
    }

    private fun createDataSource(packagePath: String): DataSource {
        val githubUrl = GithubUrlImpl(packagePath)
        githubUrl.parse()?.let {
            return githubDataSource(it, packagePath)
        }
        return FileSystemDataSource(packagePath)
    }

    private fun githubDataSource(
        pathDescription: GithubUrl.PathDescription,
        packagePath: String
    ): GithubDataSource {
        val key = githubKey ?: throw IllegalStateException("Github key is null")
        if (pathDescription.hasPart(
                Kind.USER_NAME,
                Kind.PROJECT_NAME,
                Kind.BRANCH,
                Kind.PATH
            )
        ) return GithubDataSource(
            GithubDataSource.DataSourceConfig(
                key,
                fileExtension = "java",
                userName = pathDescription.readPart(Kind.USER_NAME),
                projectName = pathDescription.readPart(Kind.PROJECT_NAME),
                path = pathDescription.readPart(Kind.PATH),
                branch = pathDescription.readPart(Kind.BRANCH)
            )
        )
        throw IllegalArgumentException("Invalid github path $packagePath")
    }
}

