package com.kondenko.pocketwaka.screens.summary

import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State

sealed class SummaryState(data: List<SummaryUiModel>?) : State<List<SummaryUiModel>>(data) {
    class ConnectRepo(val url: String, data: List<SummaryUiModel>?) : SummaryState(data)
    object EmptyRange : SummaryState(null)
}