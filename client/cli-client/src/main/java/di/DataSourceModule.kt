package di

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.githuburl.GithubUrl
import com.github.e13mort.githuburl.GithubUrlImpl
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import java.lang.IllegalArgumentException

@Module
class DataSourceModule(factory: LaunchCommand) : FactoryModule(factory) {

    @Provides
    fun dataSource(): DataSource {
        val packagePath = factory.sourcesPath
        val githubUrl = GithubUrlImpl(packagePath)
        githubUrl.parse()?.let {
            return githubDataSource(it, packagePath)
        }
        return FileSystemDataSource(packagePath)
    }

    private fun githubDataSource(
        pathDescription: GithubUrl.PathDescription,
        packagePath: String
    ): GithubDataSource {
        val key = factory.githubKey ?: throw IllegalStateException("Github key is null")
        if (pathDescription.hasPart(
                GithubUrl.PathDescription.Kind.USER_NAME,
                GithubUrl.PathDescription.Kind.PROJECT_NAME,
                GithubUrl.PathDescription.Kind.BRANCH,
                GithubUrl.PathDescription.Kind.PATH
            )
        ) return GithubDataSource(
            GithubDataSource.DataSourceConfig(
                key,
                fileExtension = "java",
                userName = pathDescription.readPart(GithubUrl.PathDescription.Kind.USER_NAME),
                projectName = pathDescription.readPart(GithubUrl.PathDescription.Kind.PROJECT_NAME),
                path = pathDescription.readPart(GithubUrl.PathDescription.Kind.PATH),
                branch = pathDescription.readPart(GithubUrl.PathDescription.Kind.BRANCH)
            )
        )
        throw IllegalArgumentException("Invalid github path $packagePath")
    }
}