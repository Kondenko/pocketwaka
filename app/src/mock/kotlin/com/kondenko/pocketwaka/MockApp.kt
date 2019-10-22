package com.kondenko.pocketwaka

import com.kondenko.pocketwaka.di.mockModules
import org.koin.core.context.loadKoinModules

class MockApp : App() {

    override fun onCreate() {
        super.onCreate()
        loadKoinModules(mockModules)
    }

}