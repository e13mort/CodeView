import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend

fun main(params: Array<String>) {
    val factory = CodeViewFactory()
    factory.main(params)
    factory.createCodeView().run()
}

class CodeViewFactory : NoRunCliktCommand() {
    private val packagePath: String by option("--package", help = "Package path").default(".")

    private val outputFileName: String by option("--out-name", help = "Output file name").default("output")

    fun createCodeView(): CodeView {
        return CodeView(
            createDataSource(packagePath),
            createFrontend(),
            createBackend(),
            createOutput(outputFileName)
        )
    }

    private fun createBackend() = JavaBackend()

    private fun createFrontend() = PulmFrontend()

    private fun createOutput(outputFileName: String) = SimpleFileOutput(outputFileName)

    private fun createDataSource(packagePath: String) = FileSystemDataSource(packagePath)
}

