package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.PlainCVInput
import dagger.Module
import dagger.Provides

@Module
class KtorCacheModule {

    @Provides
    fun input() : CVInput {
        return PlainCVInput()
    }
}