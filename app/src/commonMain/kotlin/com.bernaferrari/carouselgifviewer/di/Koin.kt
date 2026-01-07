package com.bernaferrari.carouselgifviewer.di

import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.DictDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val dataModule = module {
    single<DictDataSource> { DatabaseDataSource() }
    single { GifRepository(get()) }
}

val viewModelModule = module {
    factory { MainViewModel(get()) }
}

fun initKoin(
    block: org.koin.core.KoinApplication.() -> Unit = {}
) {
    startKoin {
        block()
        modules(
            dataModule,
            viewModelModule
        )
    }
}
