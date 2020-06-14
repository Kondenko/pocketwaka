package com.kondenko.pocketwaka.data.user.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResponse(
    @SerializedName("data")
    val user: User
) : Parcelable