package com.kondenko.pocketwaka.screens.daily

import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.screens.State

sealed class SummaryState(data: Summary?) : State<Summary>(data) {
    sealed class Empty : SummaryState(null) {
        object EmptyDate : Empty()
        object EmptyAccount : Empty()
    }
}