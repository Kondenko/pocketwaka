package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

data class CommitServerModel(
      val hash: String,
      val branch: String,
      val message: String,
      @SerializedName("author_date")
      val authorDate: String,
      @SerializedName("total_seconds")
      val totalSeconds: Int
)