package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

data class CommitsResponse(
      val error: String? = null,
      val project: Project,
      val author: Any?,
      val commits: List<CommitServerModel>,
      @SerializedName("next_page")
      val nextPage: Int,
      @SerializedName("next_page_url")
      val nextPageUrl: String,
      val page: Int?,
      @SerializedName("prev_page")
      val prevPage: Any?,
      @SerializedName("prev_page_url")
      val prevPageUrl: Any?,
      val status: String,
      @SerializedName("total_pages")
      val totalPages: Int
)