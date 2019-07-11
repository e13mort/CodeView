import com.github.e13mort.codeview.Output
import io.reactivex.Completable
import java.io.FileWriter

class SimpleFileOutput(private val name: String): Output {
    private val ext = "pulm"

    override fun save(data: String): Completable {
        return Completable.fromAction {
            val fileWriter = FileWriter("$name.$ext")
            fileWriter.write(data)
            fileWriter.flush()
            fileWriter.close()
        }
    }
}