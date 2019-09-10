package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

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