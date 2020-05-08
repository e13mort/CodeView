package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.client.ktor.di.*
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.rx2.await
import kotlinx.html.body
import kotlinx.html.img
import kotlinx.html.p

fun main() {
    embeddedServer(Netty) {
        init(this)
    }.start(wait = true)
}

fun Application.main() { init(this) }

fun init(app: Application) {
    val context = EnvironmentAppContext()
    val codeView = DaggerKtorComponent.builder()
        .ktorBackendModule(KtorBackendModule(context))
        .ktorFrontendModule(KtorFrontendModule())
        .ktorImageOutputModule(KtorImageOutputModule())
        .ktorCacheModule(KtorCacheModule(context))
        .ktorLogModule(KtorLogModule(context))
        .gitDataSourceModule(GitDataSourceModule(context.gitCachePath()))
        .build().codeView()

    app.routing {
        get("/") {
            call.respondHtml {
                body {
                    p {
                        +"CodeView"
                    }
                    img {
                        src = "diagram/test"
                    }
                }
            }
        }

        route("diagram") {
            get("{diagram-id}") {
                val diagramId = call.parameters["diagram-id"]
                diagramId?.let {
                    call.respondOutputStream(ContentType.Image.PNG, HttpStatusCode.OK) {
                        codeView.run(diagramId)
                            .onErrorReturn { wrapException(it) }
                            .await().copyTo(this)
                    }
                }
            }
        }
    }
}

private fun wrapException(it: Throwable) : KtorResult {
    return if (it is CVTransformation.TransformOperation.LongOperationException) {
        PredefinedOperationResult(LONG_OPERATION_FILE_NAME)
    } else {
        PredefinedOperationResult(ERROR_OPERATION_FILE_NAME)
    }
}