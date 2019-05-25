import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend

private const val samplePath = "/Users/pavel/work/pets/MyApplication/backend/java-backend"

fun main() {
    val transformer = CodeView(
        FileSystemDataSource(samplePath),
        PulmFrontend(),
        JavaBackend(),
        SimpleFileOutput()
    )
    transformer.run()
}

