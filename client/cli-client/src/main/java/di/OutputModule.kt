package di

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.cache.ContentStorage
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
import java.nio.file.Path
import javax.inject.Named

@Module
class OutputModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun output(log: Log, @Named("output-storage") contentStorage: ContentStorage<Path>) : Output<String> {
        return EngineBasedOutput(
            createEngine(factory.outputFormat).toCached(contentStorage),
            createEngineResult(factory.outputFileName, factory.outputFormat)
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