package di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import dagger.Module
import dagger.Provides

@Module
class PredefinedModule {

    @Provides
    fun backend() : Backend {
        return JavaBackend()
    }

    @Provides
    fun frontend() : Frontend {
        return PulmFrontend()
    }
}