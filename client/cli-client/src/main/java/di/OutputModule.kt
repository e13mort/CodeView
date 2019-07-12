package di

import factory.LaunchCommand
import PNGPumlFileOutput
import SimpleFileOutput
import com.github.e13mort.codeview.Output
import dagger.Module
import dagger.Provides
import factory.LaunchCommand.OutputFormat
import factory.LaunchCommand.OutputFormat.PNG
import factory.LaunchCommand.OutputFormat.PUML

@Module
class OutputModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun output() : Output<String> {
        return createOutput(factory.outputFileName, factory.outputFormat)
    }

    private fun createOutput(outputFileName: String, outputFormat: OutputFormat): Output<String> = when (outputFormat) {
        PUML -> SimpleFileOutput(outputFileName)
        PNG -> PNGPumlFileOutput(outputFileName)
    }
}