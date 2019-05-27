package com.github.e13mort.codeview.backend.java;

import com.github.e13mort.codeview.SourceFile;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class TemporarySourceSet {

    @NotNull
    private final CacheName cacheName;

    TemporarySourceSet(@NotNull CacheName cacheName) {
        this.cacheName = cacheName;
    }

    TemporarySources cacheFiles(@NotNull List<? extends SourceFile> files) {
        File cacheDir = prepareCacheDir();
        for (SourceFile file : files) {
            try {
                FileUtils.copyInputStreamToFile(file.read(), new File(cacheDir, cacheName.createFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TemporarySources(Paths.get(cacheDir.toURI()), cacheDir);
    }

    private File prepareCacheDir() {
        File cacheDir = new File(cacheName.createDirName());
        cacheDir.mkdir();
        return cacheDir;
    }

    static class TemporarySources implements AutoCloseable {

        private Path path;
        private final File cacheDir;

        TemporarySources(Path path, File cacheDir) {
            this.path = path;
            this.cacheDir = cacheDir;
        }

        Path files() {
            return path;
        }

        @Override
        public void close() {
            clearCache();
        }

        private void clearCache() {
            try {
                FileUtils.deleteDirectory(cacheDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
