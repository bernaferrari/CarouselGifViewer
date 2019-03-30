package com.bernaferrari.carouselgifviewer.core

import android.content.Context
import com.bernaferrari.carouselgifviewer.MainApplication
import com.bernaferrari.carouselgifviewer.main.BaseMainFragment
import com.bernaferrari.carouselgifviewer.main.DetailsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: MainApplication): Context = application.applicationContext

}

@Module
abstract class DictInjectorsModule {

    @ContributesAndroidInjector
    abstract fun dictDetailsFragment(): DetailsFragment

    @ContributesAndroidInjector
    abstract fun dictBaseFragment(): BaseMainFragment
}


@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        DictInjectorsModule::class,
        AppModule::class
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

    fun inject(app: MainApplication)
}
