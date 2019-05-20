import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend

fun main() {
    val transformer = CodeView(
        FileSystemDataSource(),
        PulmFrontend(),
        JavaBackend()
    )
    transformer.run()
}

