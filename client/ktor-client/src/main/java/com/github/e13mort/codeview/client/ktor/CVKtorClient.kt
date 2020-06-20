/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.client.ktor.AppContext.CVIntParameter
import com.github.e13mort.codeview.client.ktor.AppContext.CVStringParameter
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
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import kotlinx.coroutines.rx2.await
import kotlinx.html.body
import kotlinx.html.img
import kotlinx.html.p
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun main() {
    val environment = applicationEngineEnvironment {
        val context = EnvironmentAppContext()
        module {
            init(this, context)
        }
        context.intMaybe(CVIntParameter.PORT)?.let { it ->
            connector {
                port = it
            }
        }
        context.intMaybe(CVIntParameter.SSL_PORT)?.let { sslPort ->
            initSSL(context, sslPort)
        }

        if (connectors.isEmpty()) throw IllegalStateException("There are no connectors created")
    }
    embeddedServer(Netty, environment).start(wait = true)
}

private fun ApplicationEngineEnvironmentBuilder.initSSL(
    context: AppContext,
    sslPort: Int
) {
    val keyStoreFileName = context.string(CVStringParameter.SSL_KEY_STORE_FILE)
    val keyStorePassword = context.string(CVStringParameter.KEY_STORE_PASSWORD)
    val keyAlias = context.string(CVStringParameter.KEY_ALIAS)
    val privateKeyPassword = context.string(CVStringParameter.PRIVATE_KEY_PASSWORD)

    val keyStoreFile = File(keyStoreFileName)
    val keyStore = KeyStore.getInstance("JKS").apply {
        FileInputStream(keyStoreFile).use {
            load(it, keyStorePassword.toCharArray())
        }
        requireNotNull(getKey(keyAlias, privateKeyPassword.toCharArray()) == null) {
            "The specified key $keyAlias doesn't exist in the key store $keyStoreFileName"
        }
    }

    sslConnector(keyStore, keyAlias,
        { keyStorePassword.toCharArray() },
        { privateKeyPassword.toCharArray() }) {
        this.port = sslPort
        this.keyStorePath = keyStoreFile
    }
}

fun init(app: Application, context: AppContext) {
    val ktorCacheModule = KtorCacheModule(context)
    val codeView = DaggerKtorComponent.builder()
        .ktorBackendModule(KtorBackendModule())
        .ktorFrontendModule(KtorFrontendModule())
        .ktorImageOutputModule(KtorImageOutputModule())
        .ktorCacheModule(ktorCacheModule)
        .ktorLogModule(KtorLogModule(context))
        .gitDataSourceModule(GitDataSourceModule(context.gitCachePath()))
        .ktorDataSourceModule(KtorDataSourceModule(context))
        .build().codeView()

    val sourcesRepository = DaggerRepositoryComponent.builder()
        .ktorCacheModule(ktorCacheModule)
        .build()
        .sourcesRepository()

    app.routing {
        get("/") {
            call.respondHtml {
                body {
                    sourcesRepository.sources().forEach {
                        p {
                            +it.name
                        }
                        img {
                            src = "diagram/${it.id}"
                        }
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