package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

data class CommitServerModel(
      val id: String,
      val hash: String,
      @SerializedName("truncated_hash")
      val truncatedHash: String,
      val ref: String?,
      val message: String,
      @SerializedName("author_date")
      val authorDate: String,
      @SerializedName("author_name")
      val authorName: String,
      @SerializedName("author_username")
      val authorUsername: String,
      @SerializedName("created_at")
      val createdAt: String,
      @SerializedName("total_seconds")
      val totalSeconds: Int
)