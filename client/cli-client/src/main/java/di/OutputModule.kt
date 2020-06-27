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

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.codeview.output.EngineBasedOutput
import com.github.e13mort.codeview.output.Target
import com.github.e13mort.codeview.output.engine.OutputEngine
import com.github.e13mort.codeview.output.engine.PulmOutputEngine
import com.github.e13mort.codeview.output.engine.RawOutputEngine
import com.github.e13mort.codeview.output.toCached
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import factory.LaunchCommand.OutputFormat
import factory.LaunchCommand.OutputFormat.PNG
import factory.LaunchCommand.OutputFormat.PUML
import javax.inject.Named

@Module
class OutputModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun output(log: Log, @Named("output-storage") contentStorage: KeyValueStorage) : Output<String> {
        return EngineBasedOutput(
            createEngine(launchCommand.outputFormat).toCached(contentStorage),
            createEngineResult(launchCommand.outputFileName, launchCommand.outputFormat)
        ).withLogs(log.withTag("output"))
    }

    private fun createEngineResult(outputFileName: String, outputFormat: OutputFormat): Target<String> {
        return FileOutputResult("$outputFileName.${extension(outputFormat)}")
    }

    private fun extension(outputFormat: OutputFormat) = outputFormat.name.toLowerCase()

    private fun createEngine(outputFormat: OutputFormat): OutputEngine {
        return when(outputFormat) {
            PUML -> RawOutputEngine()
            PNG -> PulmOutputEngine()
        }
    }
}