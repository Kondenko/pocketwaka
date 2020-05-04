package com.kondenko.pocketwaka.domain.summary.model

data class Branch(val name: String, val totalSeconds: Float, val commits: List<Commit>)