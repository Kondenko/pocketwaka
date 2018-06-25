package com.kondenko.pocketwaka

import com.kondenko.pocketwaka.di.mockModulesList
import org.koin.standalone.StandAloneContext

class MockApp : DebugApp() {
    override fun onCreate() {
        super.onCreate()
        StandAloneContext.loadKoinModules(mockModulesList(this))
    }
}