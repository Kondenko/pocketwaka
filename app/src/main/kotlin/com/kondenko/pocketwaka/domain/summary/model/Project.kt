package com.kondenko.pocketwaka.domain.summary.model

data class Project(
        val name: String,
        val timeTracked: String?,
        val isRepoConnected: Boolean,
        val branches: List<Branch>
)