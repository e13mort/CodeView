package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.DataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class KtorDataSourceModule {

    @Singleton
    @Provides
    fun dataSource(): DataSource {
        return DataSource.EMPTY
    }
}