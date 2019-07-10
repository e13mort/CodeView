package di

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.githuburl.GithubUrlImpl
import dagger.Module
import dagger.Provides
import factory.LaunchCommand

@Module
class DataSourceModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun dataSource(): DataSource {
        val packagePath = factory.sourcesPath
        val githubUrl = GithubUrlImpl(packagePath)
        if (githubUrl.canParse(packagePath)) return githubDataSource()
        return FileSystemDataSource()
    }

    private fun githubDataSource(): GithubDataSource {
        val key = factory.githubKey ?: throw IllegalStateException("Github key is null")
        return GithubDataSource(
            GithubDataSource.DataSourceConfig("java", key)
        )
    }
}