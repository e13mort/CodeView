package di

import com.github.e13mort.codeview.CodeView
import dagger.Component

@Component(modules = [PredefinedModule::class, OutputModule::class, DataSourceModule::class])
interface CliComponent {
    fun codeView() : CodeView<String>
}