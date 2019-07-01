import di.DaggerCliComponent
import di.DataSourceModule
import di.OutputModule
import di.PredefinedModule
import factory.LaunchCommand

fun main(params: Array<String>) {
    val factory = LaunchCommand()
    factory.main(params)

    DaggerCliComponent.builder()
        .predefinedModule(PredefinedModule())
        .dataSourceModule(DataSourceModule(factory))
        .outputModule(OutputModule(factory))
        .build()
        .codeView()
        .run()
}
