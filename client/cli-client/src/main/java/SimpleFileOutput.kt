import com.github.e13mort.codeview.Output
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileWriter

class SimpleFileOutput(private val name: String): Output<String> {
    private val ext = "pulm"

    override fun save(data: String): Single<String> {
        return Single.fromCallable {
            val file = File("$name.$ext")
            val fileWriter = FileWriter(file)
            fileWriter.write(data)
            fileWriter.flush()
            fileWriter.close()
            file.absolutePath
        }
    }
}