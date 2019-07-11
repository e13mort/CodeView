package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.Output
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondOutputStream
import io.reactivex.Completable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sourceforge.plantuml.SourceStringReader

class ImageOutput(
    private val pipelineContext: CoroutineScope,
    private val call: ApplicationCall
) : Output {

    override fun save(data: String) : Completable {
        return Completable.fromAction {
            pipelineContext.launch { writeDiagramToResponse(data) }
        }
    }

    private suspend fun writeDiagramToResponse(data: String) {
        call.respondOutputStream(ContentType.Image.PNG, HttpStatusCode.OK) {
            this.let {
                try {
                    SourceStringReader(data).outputImage(this)
                } finally {
                    flush()
                    close()
                }
            }
        }
    }
}