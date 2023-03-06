package com.kondenko.pocketwaka.data.user.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("color_scheme")
    val colorScheme: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("date_format")
    val dateFormat: String,
    @SerializedName("default_dashboard_range")
    val defaultDashboardRange: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_public")
    val emailPublic: Boolean,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("has_premium_features")
    val hasPremiumFeatures: Boolean,
    @SerializedName("human_readable_website")
    val humanReadableWebsite: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("is_email_confirmed")
    val isEmailConfirmed: Boolean,
    @SerializedName("is_hireable")
    val isHireable: Boolean,
    @SerializedName("languages_used_public")
    val languagesUsedPublic: Boolean,
    @SerializedName("last_heartbeat_at")
    val lastHeartbeatAt: String,
    @SerializedName("last_plugin")
    val lastPlugin: String,
    @SerializedName("last_plugin_name")
    val lastPluginName: String,
    @SerializedName("last_project")
    val lastProject: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("logged_time_public")
    val loggedTimePublic: Boolean,
    @SerializedName("modified_at")
    val modifiedAt: String,
    @SerializedName("needs_payment_method")
    val needsPaymentMethod: Boolean,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("photo_public")
    val photoPublic: Boolean,
    @SerializedName("plan")
    val plan: String,
    @SerializedName("show_machine_name_ip")
    val showMachineNameIp: Boolean,
    @SerializedName("time_format_24hr")
    val timeFormat24hr: Boolean,
    @SerializedName("timeout")
    val timeout: Int,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("website")
    val website: String,
    @SerializedName("weekday_start")
    val weekdayStart: Int,
    @SerializedName("writes_only")
    val writesOnly: Boolean
) : Parcelable