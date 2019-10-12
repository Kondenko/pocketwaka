package com.kondenko.pocketwaka.domain.summary.model

data class Branch(val name: String, val timeTracked: String, val commits: List<Commit>)