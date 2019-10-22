package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

data class Repository(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("default_branch")
        val defaultBranch: String,
        val description: String,
        @SerializedName("fork_count")
        val forkCount: Int,
        @SerializedName("full_name")
        val fullName: String,
        val homepage: Any?,
        @SerializedName("html_url")
        val htmlUrl: String,
        val id: String,
        @SerializedName("is_fork")
        val isFork: Boolean,
        @SerializedName("is_private")
        val isPrivate: Boolean,
        @SerializedName("last_synced_at")
        val lastSyncedAt: String,
        @SerializedName("modified_at")
        val modifiedAt: String,
        val name: String,
        val provider: String,
        @SerializedName("star_count")
        val starCount: Int,
        val url: String,
        @SerializedName("watch_count")
        val watchCount: Int
)