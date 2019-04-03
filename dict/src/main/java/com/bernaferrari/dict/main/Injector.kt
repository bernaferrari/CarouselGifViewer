package com.bernaferrari.dict.main

import android.app.Activity
import com.bernaferrari.dict.data.DatabaseDataSource
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

fun DictActivity.inject() {
    DaggerAboutComponent.builder()
        .activity(this)
        .build()
        .inject(this)
}


/**
 * Scope for a feature module.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope

interface BaseComponent<T> {

    fun inject(target: T)
}


/**
 * Base dagger component for use in activities.
 */
interface BaseFragmentComponent<T : Activity> : BaseComponent<T>


/**
 * Dagger component for `about` feature module.
 */
@Component(modules = [DictModule::class])
@FeatureScope
interface AboutComponent :
    BaseFragmentComponent<DictActivity> {

    @Component.Builder
    interface Builder {

        fun build(): AboutComponent

        @BindsInstance
        fun activity(activity: DictActivity): Builder
    }

    fun dictRepository(): DatabaseDataSource
}

@Module
class DictModule {

    @Provides
    fun provideDictRepository() = DatabaseDataSource()

}
