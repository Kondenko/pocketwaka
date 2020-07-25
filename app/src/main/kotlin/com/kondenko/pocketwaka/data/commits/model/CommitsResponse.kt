package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

data class CommitsResponse(
      val error: String? = null,
      val commits: List<CommitServerModel>,
      @SerializedName("total_pages")
      val totalPages: Int
)