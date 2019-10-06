package com.github.e13mort.codeview.cache;

import com.github.e13mort.codeview.SourceFile;
import com.github.e13mort.codeview.Sources;
import io.reactivex.Single;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TmpDirBasedCache implements Cache {

    @NotNull
    private final CacheName cacheName;
    private final String dirName;

    public TmpDirBasedCache(@NotNull CacheName cacheName, @NotNull String cacheDirName) {
        this.cacheName = cacheName;
        this.dirName = cacheDirName;
    }

    @NotNull
    @Override
    public Single<TemporarySources> cacheSources(@NotNull Sources sources) {
        return sources.sources().toList().map(this::cacheFiles);
    }

    private TemporarySources cacheFiles(@NotNull List<? extends SourceFile> files) {
        File cacheDir = prepareCacheDir();
        for (SourceFile file : files) {
            try {
                File destination = new File(cacheDir, cacheName.createFileName());
                destination.deleteOnExit();
                FileUtils.copyInputStreamToFile(file.read(), destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TemporarySourcesImpl(Paths.get(cacheDir.toURI()));
    }

    private File prepareCacheDir() {
        File cacheDir = new File(dirName);
        cacheDir.mkdir();
        cacheDir.deleteOnExit();
        return cacheDir;
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
