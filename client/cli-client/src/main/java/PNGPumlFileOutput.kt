import com.github.e13mort.codeview.Output
import io.reactivex.Completable
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.FileOutputStream

class PNGPumlFileOutput(private val name: String) : Output {

    override fun save(data: String): Completable {
        return Completable.fromAction {
            SourceStringReader(data)
                .outputImage(FileOutputStream("$name.png"), FileFormatOption(FileFormat.PNG))
        }
    }
}