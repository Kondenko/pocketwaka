package com.kondenko.pocketwaka.data.stats.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Stats {

    @SerializedName("range")
    @Expose
    var range: String? = null

    @SerializedName("best_day")
    @Expose
    var bestDay: BestDay? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("daily_average")
    @Expose
    var dailyAverage: Int? = null

    @SerializedName("days_including_holidays")
    @Expose
    var daysIncludingHolidays: Int? = null

    @SerializedName("days_minus_holidays")
    @Expose
    var daysMinusHolidays: Int? = null

    @SerializedName("editors")
    @Expose
    var editors: List<Editor>? = null

    @SerializedName("end")
    @Expose
    var end: String? = null

    @SerializedName("holidays")
    @Expose
    var holidays: Int? = null

    @SerializedName("human_readable_daily_average")
    @Expose
    var humanReadableDailyAverage: String? = null

    @SerializedName("human_readable_total")
    @Expose
    var humanReadableTotal: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("is_already_updating")
    @Expose
    var isAlreadyUpdating: Boolean? = null

    @SerializedName("is_stuck")
    @Expose
    var isStuck: Boolean? = null

    @SerializedName("is_up_to_date")
    @Expose
    var isUpToDate: Boolean? = null

    @SerializedName("languages")
    @Expose
    var languages: List<Language>? = null

    @SerializedName("modified_at")
    @Expose
    var modifiedAt: String? = null

    @SerializedName("operating_systems")
    @Expose
    var operatingSystems: List<OperatingSystem>? = null

    @SerializedName("project")
    @Expose
    var project: Any? = null

    @SerializedName("projects")
    @Expose
    var projects: List<Project>? = null

    @SerializedName("start")
    @Expose
    var start: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("timeout")
    @Expose
    var timeout: Int? = null

    @SerializedName("timezone")
    @Expose
    var timezone: String? = null

    @SerializedName("total_seconds")
    @Expose
    var totalSeconds: Double? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("writes_only")
    @Expose
    var writesOnly: Boolean? = null

}
