package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.summary.usecase.GetDefaultSummaryRange
import com.kondenko.pocketwaka.utils.WakaLog

class SummaryRangeViewModel(getDefaultSummaryRange: UseCaseSingle<Nothing?, SummaryDate>) : ViewModel() {

    private val dates = MutableLiveData<SummaryDate>()

    init {
        getDefaultSummaryRange(
              onError = WakaLog::e,
              onSuccess = dates::postValue
        )
    }

    fun dateChanges(): LiveData<SummaryDate> = dates

}