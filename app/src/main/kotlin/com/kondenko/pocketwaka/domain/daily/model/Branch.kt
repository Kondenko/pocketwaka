package com.kondenko.pocketwaka.domain.daily.model

data class Branch(val name: String, val timeTracked: String, val commits: List<Commit>)