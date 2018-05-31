package com.kondenko.pocketwaka.domain.stats.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BestDay(
        val date: String?,
        val totalSeconds: Int?
) : Parcelable