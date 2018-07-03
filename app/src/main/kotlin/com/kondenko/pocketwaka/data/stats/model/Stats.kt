package com.kondenko.pocketwaka.data.stats.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Stats : Parcelable {


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

    @SerializedName("range")
    @Expose
    var range: String? = null

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
    var totalSeconds: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("writes_only")
    @Expose
    var writesOnly: Boolean? = null


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(bestDay)
        dest.writeValue(createdAt)
        dest.writeValue(dailyAverage)
        dest.writeValue(daysIncludingHolidays)
        dest.writeValue(daysMinusHolidays)
        dest.writeList(editors)
        dest.writeValue(end)
        dest.writeValue(holidays)
        dest.writeValue(humanReadableDailyAverage)
        dest.writeValue(humanReadableTotal)
        dest.writeValue(id)
        dest.writeValue(isAlreadyUpdating)
        dest.writeValue(isStuck)
        dest.writeValue(isUpToDate)
        dest.writeList(languages)
        dest.writeValue(modifiedAt)
        dest.writeList(operatingSystems)
        dest.writeValue(project)
        dest.writeList(projects)
        dest.writeValue(range)
        dest.writeValue(start)
        dest.writeValue(status)
        dest.writeValue(timeout)
        dest.writeValue(timezone)
        dest.writeValue(totalSeconds)
        dest.writeValue(userId)
        dest.writeValue(username)
        dest.writeValue(writesOnly)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Stats(bestDay=$bestDay, createdAt=$createdAt, dailyAverage=$dailyAverage, daysIncludingHolidays=$daysIncludingHolidays, daysMinusHolidays=$daysMinusHolidays, editors=$editors, end=$end, holidays=$holidays, humanReadableDailyAverage=$humanReadableDailyAverage, humanReadableTotal=$humanReadableTotal, id=$id, isAlreadyUpdating=$isAlreadyUpdating, isStuck=$isStuck, isUpToDate=$isUpToDate, languages=$languages, modifiedAt=$modifiedAt, operatingSystems=$operatingSystems, project=$project, projects=$projects, range=$range, start=$start, status=$status, timeout=$timeout, timezone=$timezone, totalSeconds=$totalSeconds, userId=$userId, username=$username, writesOnly=$writesOnly)"
    }

    companion object {

        val CREATOR: Parcelable.Creator<Stats> = object : Parcelable.Creator<Stats> {
            override fun createFromParcel(`in`: Parcel): Stats {
                val instance = Stats()
                instance.bestDay = `in`.readValue(BestDay::class.java.classLoader) as BestDay
                instance.createdAt = `in`.readValue(String::class.java.classLoader) as String
                instance.dailyAverage = `in`.readValue(Int::class.java.classLoader) as Int
                instance.daysIncludingHolidays = `in`.readValue(Int::class.java.classLoader) as Int
                instance.daysMinusHolidays = `in`.readValue(Int::class.java.classLoader) as Int
                `in`.readList(instance.editors, Editor::class.java.classLoader)
                instance.end = `in`.readValue(String::class.java.classLoader) as String
                instance.holidays = `in`.readValue(Int::class.java.classLoader) as Int
                instance.humanReadableDailyAverage = `in`.readValue(String::class.java.classLoader) as String
                instance.humanReadableTotal = `in`.readValue(String::class.java.classLoader) as String
                instance.id = `in`.readValue(String::class.java.classLoader) as String
                instance.isAlreadyUpdating = `in`.readValue(Boolean::class.java.classLoader) as Boolean
                instance.isStuck = `in`.readValue(Boolean::class.java.classLoader) as Boolean
                instance.isUpToDate = `in`.readValue(Boolean::class.java.classLoader) as Boolean
                `in`.readList(instance.languages, Language::class.java.classLoader)
                instance.modifiedAt = `in`.readValue(String::class.java.classLoader) as String
                `in`.readList(instance.operatingSystems, OperatingSystem::class.java.classLoader)
                instance.project = `in`.readValue(Any::class.java.classLoader)
                `in`.readList(instance.projects, Project::class.java.classLoader)
                instance.range = `in`.readValue(String::class.java.classLoader) as String
                instance.start = `in`.readValue(String::class.java.classLoader) as String
                instance.status = `in`.readValue(String::class.java.classLoader) as String
                instance.timeout = `in`.readValue(Int::class.java.classLoader) as Int
                instance.timezone = `in`.readValue(String::class.java.classLoader) as String
                instance.totalSeconds = `in`.readValue(Int::class.java.classLoader) as Int
                instance.userId = `in`.readValue(String::class.java.classLoader) as String
                instance.username = `in`.readValue(String::class.java.classLoader) as String
                instance.writesOnly = `in`.readValue(Boolean::class.java.classLoader) as Boolean
                return instance
            }

            override fun newArray(size: Int): Array<Stats?> {
                return arrayOfNulls(size)
            }

        }
    }

}
