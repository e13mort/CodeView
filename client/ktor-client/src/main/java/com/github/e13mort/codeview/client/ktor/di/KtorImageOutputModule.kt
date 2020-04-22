package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.output.EngineBasedOutput
import com.github.e13mort.codeview.output.engine.PulmOutputEngine
import com.github.e13mort.codeview.output.toCached
import dagger.Module
import dagger.Provides
import java.io.OutputStream
import javax.inject.Named

@Module
class KtorImageOutputModule {

    @Provides
    fun output(@Named(DI_KEY_OUTPUT_STORAGE) storage: ContentStorage): Output<KtorResult> {
        return EngineBasedOutput(
            PulmOutputEngine().toCached(storage),
            MemoryCacheTarget())
    }
}

interface KtorResult {
    fun copyTo(outputStream: OutputStream)
}
