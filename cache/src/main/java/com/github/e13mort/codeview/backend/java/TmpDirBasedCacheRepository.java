package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.CacheRepository;
import com.github.e13mort.codeview.SourceFile;
import io.reactivex.Single;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TmpDirBasedCacheRepository implements CacheRepository {

    @NotNull
    private final CacheName cacheName;

    public TmpDirBasedCacheRepository(@NotNull CacheName cacheName) {
        this.cacheName = cacheName;
    }

    @NotNull
    @Override
    public Single<TemporarySources> cacheSources(@NotNull List<? extends SourceFile> sources) {
        return Single.fromCallable(() -> cacheFiles(sources));
    }

    private TemporarySources cacheFiles(@NotNull List<? extends SourceFile> files) {
        File cacheDir = prepareCacheDir();
        for (SourceFile file : files) {
            try {
                FileUtils.copyInputStreamToFile(file.read(), new File(cacheDir, cacheName.createFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TemporarySourcesImpl(Paths.get(cacheDir.toURI()));
    }

    private File prepareCacheDir() {
        File cacheDir = new File(cacheName.createDirName());
        cacheDir.mkdir();
        return cacheDir;
    }

    @Override
    public void clear() {
        try {
            // TODO: 2019-07-26 store cache name
            FileUtils.deleteDirectory(new File(cacheName.createDirName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TemporarySourcesImpl implements TemporarySources {

        private Path path;

        TemporarySourcesImpl(Path path) {
            this.path = path;
        }

        @NotNull
        public Path files() {
            return path;
        }
    }
}
