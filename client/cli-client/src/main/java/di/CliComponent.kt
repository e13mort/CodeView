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

package di

import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.ClassesView
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import com.github.e13mort.codeview.datasource.github.di.GithubModule
import dagger.*
import factory.LaunchCommand
import java.nio.file.Path

@Component(
    modules = [
        DataModule::class,
        OutputModule::class,
        InputModule::class,
        DecisionModule::class,
        GithubModule::class,
        GithubDependencies::class,
        GitDataSourceModule::class
    ]
)
interface CliComponent {
    fun codeView(): CodeView<String>

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun launchCommand(launchCommand: LaunchCommand): Builder

        @BindsInstance
        fun root(root: Path): Builder

        fun build(): CliComponent
    }
}

@Module
abstract class DecisionModule {
    @Binds
    abstract fun createCodeView(view: ClassesView<String>): CodeView<String>
}