package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName


data class Commits(
        val error: String? = null,
        val project: Project,
        val author: Any?,
        val commits: List<Commit>,
        @SerializedName("next_page")
        val nextPage: Int,
        @SerializedName("next_page_url")
        val nextPageUrl: String,
        val page: Int,
        @SerializedName("prev_page")
        val prevPage: Any?,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any?,
        val status: String,
        @SerializedName("total_pages")
        val totalPages: Int
)

data class Commit(
        @SerializedName("author_avatar_url")
        val authorAvatarUrl: String,
        @SerializedName("author_date")
        val authorDate: String,
        @SerializedName("author_email")
        val authorEmail: String,
        @SerializedName("author_html_url")
        val authorHtmlUrl: String,
        @SerializedName("author_name")
        val authorName: String,
        @SerializedName("author_url")
        val authorUrl: String,
        @SerializedName("author_username")
        val authorUsername: String,
        @SerializedName("committer_avatar_url")
        val committerAvatarUrl: String,
        @SerializedName("committer_date")
        val committerDate: String,
        @SerializedName("committer_email")
        val committerEmail: String,
        @SerializedName("committer_html_url")
        val committerHtmlUrl: String,
        @SerializedName("committer_name")
        val committerName: String,
        @SerializedName("committer_url")
        val committerUrl: String,
        @SerializedName("committer_username")
        val committerUsername: String,
        @SerializedName("created_at")
        val createdAt: String,
        val hash: String,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("human_readable_total")
        val humanReadableTotal: String,
        @SerializedName("human_readable_total_with_seconds")
        val humanReadableTotalWithSeconds: String,
        val id: String,
        val message: String,
        val ref: String?,
        @SerializedName("total_seconds")
        val totalSeconds: Int,
        @SerializedName("truncated_hash")
        val truncatedHash: String,
        val url: String
)

data class Project(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("html_escaped_name")
        val htmlEscapedName: String,
        val id: String,
        val name: String,
        val repository: Repository,
        val url: String
)

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