package com.kondenko.pocketwaka.data.commits.model

import com.google.gson.annotations.SerializedName

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