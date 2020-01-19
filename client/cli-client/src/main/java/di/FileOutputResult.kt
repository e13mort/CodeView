package di

import com.github.e13mort.codeview.output.Target
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileOutputResult(private val fileName: String) :
    Target<String> {
    override fun output(): OutputStream {
        return FileOutputStream(fileName)
    }

    override fun toResult(): String {
        return File(fileName).absolutePath
    }
}