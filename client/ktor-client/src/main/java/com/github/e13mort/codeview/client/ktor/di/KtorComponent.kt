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

package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        KtorFrontendModule::class,
        KtorLogModule::class,
        KtorBackendModule::class,
        KtorImageOutputModule::class,
        KtorCacheModule::class,
        KtorSourcesModule::class,
        GitDataSourceModule::class
    ]
)
interface KtorComponent {
    fun codeView(): CodeView<KtorResult>
}
