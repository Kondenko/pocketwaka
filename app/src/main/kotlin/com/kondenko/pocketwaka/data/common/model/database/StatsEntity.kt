package com.kondenko.pocketwaka.data.common.model.database

import com.google.gson.annotations.SerializedName

/**
 * Represents Editors, Languages and other similar entities
 * with the same fields found in Wakatime API.
 */
data class StatsEntity(
      @SerializedName("total_seconds")
      val totalSeconds: Float? = null,
      val hours: Int? = null,
      val minutes: Int? = null,
      val name: String? = null,
      val percent: Double? = null,
      val text: String? = null
)