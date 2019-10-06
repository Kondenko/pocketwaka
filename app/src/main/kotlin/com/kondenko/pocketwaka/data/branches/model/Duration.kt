package com.kondenko.pocketwaka.data.branches.model

import com.google.gson.annotations.SerializedName

data class Duration(
    val branch: String,
    @SerializedName("created_at")
    val createdAt: String,
    val dependencies: List<Any>,
    val duration: Double,
    val entity: String,
    val id: String,
    val language: String,
    @SerializedName("machine_name_id")
    val machineNameId: String,
    val project: String,
    val time: Double,
    val type: String,
    @SerializedName("user_id")
    val userId: String,
    val cursorpos: Any?,
    val lineno: Any?
)