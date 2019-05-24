import com.github.e13mort.codeview.Output
import java.io.FileWriter

class SimpleFileOutput: Output {
    override fun save(data: String) {
        val fileWriter = FileWriter("test.pulm")
        fileWriter.write(data)
        fileWriter.flush()
        fileWriter.close()
    }
}