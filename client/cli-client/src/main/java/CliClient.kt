import di.DaggerCliComponent
import di.InputModule
import di.OutputModule
import di.PredefinedModule
import factory.LaunchCommand
import java.nio.file.FileSystems
import java.nio.file.Files

const val SYSTEM_PROPERTY_HOME = "user.home"
const val WORKING_DIR_NAME = "cv_cache"

private val workingDirectory by lazy {
    FileSystems.getDefault().getPath(System.getProperty(SYSTEM_PROPERTY_HOME)).resolve(WORKING_DIR_NAME)
}

fun main(params: Array<String>) {
    val launchCommand = LaunchCommand()
    launchCommand.main(params)

    if (!Files.exists(workingDirectory)) Files.createDirectory(workingDirectory)

    DaggerCliComponent.builder()
        .predefinedModule(PredefinedModule(workingDirectory))
        .inputModule(InputModule(launchCommand, workingDirectory))
        .outputModule(OutputModule(launchCommand))
        .build()
        .codeView()
        .run(launchCommand.sourcesPath)
        .subscribe(::printResult, ::println)
}

private fun printResult(res: String) {
    println(res)
}
