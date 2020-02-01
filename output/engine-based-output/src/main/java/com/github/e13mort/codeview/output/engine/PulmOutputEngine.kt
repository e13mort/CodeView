package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.OutputStream

class PulmOutputEngine : OutputEngine {

    override fun saveDataToOutputStream(data: CVTransformation.TransformOperation<StoredObject>, outputStream: OutputStream) {
        SourceStringReader(data.run().asString())
            .outputImage(outputStream,
                FileFormatOption(FileFormat.PNG)
            )
    }
}