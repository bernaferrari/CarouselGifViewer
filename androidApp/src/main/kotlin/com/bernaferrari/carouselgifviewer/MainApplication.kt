package com.bernaferrari.carouselgifviewer

import android.app.Application
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import com.bernaferrari.carouselgifviewer.data.createDataStore
import com.bernaferrari.carouselgifviewer.data.createDownloadManager
import com.bernaferrari.carouselgifviewer.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {
    val settingsRepository by lazy { SettingsRepository(createDataStore(applicationContext)) }
    val downloadManager by lazy { createDownloadManager(applicationContext) }


    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
