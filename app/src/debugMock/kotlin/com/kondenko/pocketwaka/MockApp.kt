package com.kondenko.pocketwaka

import com.kondenko.pocketwaka.dagger.components.StatsComponent
import com.kondenko.pocketwaka.dagger.modules.MockStatsModule

class MockApp : DebugApp() {

    override fun statsComponent(): StatsComponent {
        return statsComponent.initIfNull(appComponent.plusStats(MockStatsModule())) {
            statsComponent = it
        }
    }
}