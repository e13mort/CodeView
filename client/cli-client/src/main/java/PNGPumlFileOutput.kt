import com.github.e13mort.codeview.Output
import io.reactivex.Single
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.File
import java.io.FileOutputStream

class PNGPumlFileOutput(private val name: String) : Output<String> {

    override fun save(data: String): Single<String> {
        return Single.just(File("$name.png"))
            .doOnEvent { file, _ ->
                SourceStringReader(data).outputImage(FileOutputStream(file), FileFormatOption(FileFormat.PNG))
            }.map { it.absolutePath }
    }
}