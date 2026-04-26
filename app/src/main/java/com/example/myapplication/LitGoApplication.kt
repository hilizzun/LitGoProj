package com.example.myapplication

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class LitGoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
            MapKitFactory.initialize(this)
        }
    }
}
