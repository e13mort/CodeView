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

import com.github.e13mort.codeview.ClassesView
import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.PUMLView
import com.github.e13mort.codeview.client.cli.CodeViewTypeResolver
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import javax.inject.Provider

@Module
class CodeViewTypeResolverModule {
    @Provides
    fun createCodeView(
        classesView: Provider<ClassesView<String>>,
        pumlView: Provider<PUMLView<String>>,
        codeViewTypeResolver: CodeViewTypeResolver,
        launchCommand: LaunchCommand
    ): CodeView<String> {
        return when (codeViewTypeResolver.resolve(launchCommand.sourcesPath)) {
            CodeViewTypeResolver.CodeViewType.CLASSES -> classesView.get()
            CodeViewTypeResolver.CodeViewType.PUML -> pumlView.get()
        }
    }
}