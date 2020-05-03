package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import com.github.e13mort.codeview.datasource.filesystem.PathSourceFile
import com.github.e13mort.codeview.datasource.filesystem.FSVisitor
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import io.reactivex.Single
import java.nio.file.Path
import javax.inject.Inject

class GitDataSource @Inject constructor(
    private val remoteRepositories: RemoteRepositories,
    private val sourcesUrl: SourcesUrl,
    private val localRepositories: LocalRepositories
) : DataSource {

    override fun name(): String {
        return "GIT"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.fromCallable {
            val pathDescription = sourcesUrl.parse(path) ?: throw IllegalArgumentException("path $path can not be parsed")
            val targetRepo = pathDescription.readPart(Kind.GIT_URL_HTTPS)
            val targetBranch = pathDescription.readPart(Kind.BRANCH)
            val pathInRepo = pathDescription.readPart(Kind.PATH)
            remoteRepositories.remoteBranchHash(pathDescription)?.let {
                return@fromCallable GitSources(GitSources.SourcesDescription(targetRepo, it, pathInRepo), remoteRepositories, localRepositories)
            }
            throw IllegalArgumentException("invalid target branch $targetBranch")
        }
    }

    private class GitSources(
        private val description: SourcesDescription,
        private val remoteRepositories: RemoteRepositories,
        private val localRepositories: LocalRepositories
    ) : Sources {

        private val fsVisitor = FSVisitor()
        private val options = FSVisitor.Options("java", 1)

        override fun sources(): List<SourceFile> {
            clonedRepo(searchForLocalRepository()).let {
                checkout(it)
                resolveSourceFolder(it)
            }.let {
                return visitFolder(it).map(::PathSourceFile)
            }
        }

        private fun checkout(clonedRepo: RemoteRepositories.ClonedRepo) = clonedRepo.checkout(description.cloneHash)

        private fun searchForLocalRepository(): Path = localRepositories.search(description.repoUrl)

        private fun clonedRepo(localPath: Path): RemoteRepositories.ClonedRepo {
            return remoteRepositories.clone(description.repoUrl, localPath)
        }

        private fun visitFolder(it: Path): List<Path> {
            return fsVisitor.visitFolder(it, options)
        }

        private fun resolveSourceFolder(it: RemoteRepositories.ClonedRepo) = it.path().resolve(description.sourcesFolder)

        override fun name(): String {
            return description.toString()
        }

        data class SourcesDescription(
            val repoUrl: String,
            val cloneHash: String,
            val sourcesFolder: String
        )
    }
}