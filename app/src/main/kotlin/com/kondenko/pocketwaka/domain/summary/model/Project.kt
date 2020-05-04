package com.kondenko.pocketwaka.domain.summary.model

data class Project(
      val name: String,
      val totalSeconds: Float,
      val isRepoConnected: Boolean,
      val branches: List<Branch>
)