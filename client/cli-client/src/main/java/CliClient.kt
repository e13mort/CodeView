import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend

fun main(params: Array<String>) {
    val factory = CodeViewFactory()
    factory.main(params)
    factory.createCodeView().run()
}

class CodeViewFactory : NoRunCliktCommand() {
    private enum class OutputFormat {PUML, PNG}

    private val sourcesPath: String by argument("sources", help = "Path to sources root").default(".")

    private val outputFileName: String by option("--out-name", help = "Output file name").default("output")

    private val outputFormat: OutputFormat by argument("format", help = "Output format")
        .choice(OutputFormat.PNG.name.toLowerCase() to OutputFormat.PNG, OutputFormat.PUML.name.toLowerCase() to OutputFormat.PUML)
        .default(OutputFormat.PNG)

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

    private fun createOutput(outputFileName: String, outputFormat: OutputFormat): Output {
        return when (outputFormat) {
            OutputFormat.PUML -> SimpleFileOutput(outputFileName)
            OutputFormat.PNG -> PNGPumlFileOutput(outputFileName)
        }
    }

    private fun createDataSource(packagePath: String) = FileSystemDataSource(packagePath)
}

