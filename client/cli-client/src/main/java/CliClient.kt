import di.DaggerCliComponent
import di.InputModule
import di.OutputModule
import di.PredefinedModule
import factory.LaunchCommand

fun main(params: Array<String>) {
    val launchCommand = LaunchCommand()
    launchCommand.main(params)

    DaggerCliComponent.builder()
        .predefinedModule(PredefinedModule())
        .inputModule(InputModule(launchCommand))
        .outputModule(OutputModule(launchCommand))
        .build()
        .codeView()
        .run(launchCommand.sourcesPath)
        .subscribe(::printResult)
}

private fun printResult(res: String) {
    println(res)
}
