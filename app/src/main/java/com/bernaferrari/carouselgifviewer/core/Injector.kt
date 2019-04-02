package com.bernaferrari.carouselgifviewer.core

import android.content.Context
import com.bernaferrari.carouselgifviewer.MainApplication
import com.bernaferrari.carouselgifviewer.main.DatabaseDataSource
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: MainApplication): Context = application.applicationContext

}

@Module
class DictModule {

    @Singleton
    @Provides
    fun provideDictRepository() = DatabaseDataSource()

}


@Component(
    modules = [
        AppModule::class,
        DictModule::class
    ]
)
@Singleton
interface SingletonComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: MainApplication): Builder

        fun build(): SingletonComponent
    }

    fun dictRepository(): DatabaseDataSource

}

class Injector private constructor() {
    companion object {
        fun get(): SingletonComponent = MainApplication.get().component
    }
}
