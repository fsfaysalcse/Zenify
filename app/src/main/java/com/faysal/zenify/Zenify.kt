package com.faysal.zenify

import android.app.Application
import com.faysal.zenify.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class Zenify : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Koin
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@Zenify)
            // Load modules
            modules(appModules)
        }
    }
}