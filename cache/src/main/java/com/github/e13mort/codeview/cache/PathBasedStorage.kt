package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.SourceFile
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.nio.file.Files
import java.nio.file.Path

class PathBasedStorage(
    private val root: Path,
    private val registryFileName: String,
    private val cacheName: CacheName
) :
    FileStorageBasedCache.FileStorage {

    override fun search(key: String): Maybe<FileStorageBasedCache.FileStorage.FileStorageItem> {
        return Maybe.create {
            folderName(key)?.apply {
                val path = root.resolve(this)
                if (Files.exists(path)) {
                    it.onSuccess(PathBasedStorageItem(path))
                    return@create
                }
            }
            it.onComplete()
        }
    }

    override fun put(
        key: String,
        sourceFiles: Observable<SourceFile>
    ): Single<FileStorageBasedCache.FileStorage.FileStorageItem> {

        return sourceFiles.withLatestFrom(
            registerCacheFolder(key),
            BiFunction<SourceFile, Path, Path> { t1, t2 -> copyFileToCache(t1, t2) })
            .lastOrError()
            .map { PathBasedStorageItem(it) }
    }

    private fun copyFileToCache(sourceFile: SourceFile, parent: Path): Path {
        Files.copy(sourceFile.read(), parent.resolve(cacheName.createFileName()))
        return parent
    }

    private fun registerCacheFolder(key: String): Observable<Path> {
        return Observable.fromPublisher<Path> {
            try {
                ensureRootExists()
                val folderName = cacheName.createDirName()
                val map = readMap()
                map[key] = folderName
                val path = root.resolve(folderName)
                Files.createDirectory(path)
                if (Files.exists(path)) saveMap(map)
                it.onNext(path)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    private fun ensureRootExists() {
        if (!Files.exists(root)) Files.createDirectory(root)
    }

    private fun saveMap(map: MutableMap<String, String>) =
        Files.write(root.resolve(registryFileName), listOf(map.asJson()))

    private fun folderName(key: String): String? {
        return readMap()[key]
    }

    private fun readMap(): MutableMap<String, String> {
        val path = root.resolve(registryFileName)
        return if (Files.exists(path)) {
            readRegistry(path)
        } else {
            mutableMapOf()
        }
    }

    private fun readRegistry(path: Path): MutableMap<String, String> {
        val readAllLines = Files.readAllLines(path)
        val combined = readAllLines.reduce { acc, s -> acc.plus(s) }
        val json = Json(configuration = JsonConfiguration.Stable)
        return json.parse(CachedMap.serializer(), combined).data
    }

    private class PathBasedStorageItem(private val path: Path) :
        FileStorageBasedCache.FileStorage.FileStorageItem {
        override fun path(): Path = path
    }

}

@Serializable
private data class CachedMap(val data: MutableMap<String, String>)

private fun MutableMap<String, String>.asJson(): String {
    val json = Json(configuration = JsonConfiguration.Stable)
    return json.toJson(CachedMap.serializer(), CachedMap(this)).toString()
}