package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Completable
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.OutputStream

class PulmOutputEngine : OutputEngine {

    override fun saveDataToOutputStream(
        data: CVTransformation.TransformOperation<StoredObject>,
        outputStream: OutputStream
    ): Completable {
        return data
            .transform()
            .flatMapCompletable { saveResult(it.asString(), outputStream) }
    }

    private fun saveResult(string: String, outputStream: OutputStream): Completable = Completable.fromAction {
        SourceStringReader(string).outputImage(
            outputStream,
            FileFormatOption(FileFormat.PNG)
        )
    }
}