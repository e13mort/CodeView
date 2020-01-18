package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Single
import java.io.File
import java.io.FileWriter

class PulmFileOutput(private val name: String): Output<String> {
    private val ext = "pulm"

    override fun save(data: StoredObject): Single<String> {
        return Single.fromCallable {
            val file = File("$name.$ext")
            val fileWriter = FileWriter(file)
            fileWriter.write(data.asString())
            fileWriter.flush()
            fileWriter.close()
            file.absolutePath
        }
    }
}