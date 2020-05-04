package com.kondenko.pocketwaka.data.common.model.database

import com.google.gson.annotations.SerializedName

/**
 * Represents Editors, Languages and other similar entities
 * with the same fields found in Wakatime API.
 */
data class StatsEntity(
      val name: String? = null,
      @SerializedName("total_seconds")
      val totalSeconds: Float,
      val percent: Double? = null
)