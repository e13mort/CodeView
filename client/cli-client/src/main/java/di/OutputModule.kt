package di

import factory.LaunchCommand
import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.codeview.output.PNGPumlFileOutput
import com.github.e13mort.codeview.output.PulmFileOutput
import dagger.Module
import dagger.Provides
import factory.LaunchCommand.OutputFormat
import factory.LaunchCommand.OutputFormat.PNG
import factory.LaunchCommand.OutputFormat.PUML

@Module
class OutputModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun output(log: Log) : Output<String> {
        return createOutput(factory.outputFileName, factory.outputFormat).withLogs(log.withTag("output"))
    }

    private fun createOutput(outputFileName: String, outputFormat: OutputFormat): Output<String> = when (outputFormat) {
        PUML -> PulmFileOutput(outputFileName)
        PNG -> PNGPumlFileOutput(outputFileName)
    }
}