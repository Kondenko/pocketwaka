
package com.kondenko.pocketwaka.api.model.stats;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stats implements Parcelable {

    @SerializedName("best_day")
    @Expose
    public BestDay bestDay = null;
    @SerializedName("created_at")
    @Expose
    public String createdAt = null;
    @SerializedName("daily_average")
    @Expose
    public Integer dailyAverage = null;
    @SerializedName("days_including_holidays")
    @Expose
    public Integer daysIncludingHolidays = null;
    @SerializedName("days_minus_holidays")
    @Expose
    public Integer daysMinusHolidays = null;
    @SerializedName("editors")
    @Expose
    public List<Editor> editors = null;
    @SerializedName("end")
    @Expose
    public String end = null;
    @SerializedName("holidays")
    @Expose
    public Integer holidays = null;
    @SerializedName("human_readable_daily_average")
    @Expose
    public String humanReadableDailyAverage = null;
    @SerializedName("human_readable_total")
    @Expose
    public String humanReadableTotal = null;
    @SerializedName("id")
    @Expose
    public String id = null;
    @SerializedName("is_already_updating")
    @Expose
    public Boolean isAlreadyUpdating = null;
    @SerializedName("is_stuck")
    @Expose
    public Boolean isStuck = null;
    @SerializedName("is_up_to_date")
    @Expose
    public Boolean isUpToDate = null;
    @SerializedName("languages")
    @Expose
    public List<Language> languages = null;
    @SerializedName("modified_at")
    @Expose
    public String modifiedAt = null;
    @SerializedName("operating_systems")
    @Expose
    public List<OperatingSystem> operatingSystems = null;
    @SerializedName("project")
    @Expose
    public Object project = null;
    @SerializedName("projects")
    @Expose
    public List<Project> projects = null;
    @SerializedName("range")
    @Expose
    public String range = null;
    @SerializedName("start")
    @Expose
    public String start = null;
    @SerializedName("status")
    @Expose
    public String status = null;
    @SerializedName("timeout")
    @Expose
    public Integer timeout = null;
    @SerializedName("timezone")
    @Expose
    public String timezone = null;
    @SerializedName("total_seconds")
    @Expose
    public Integer totalSeconds = null;
    @SerializedName("user_id")
    @Expose
    public String userId = null;
    @SerializedName("username")
    @Expose
    public String username = null;
    @SerializedName("writes_only")
    @Expose
    public Boolean writesOnly = null;


    public final static Parcelable.Creator<Stats> CREATOR = new Creator<Stats>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Stats createFromParcel(Parcel in) {
            Stats instance = new Stats();
            instance.bestDay = ((BestDay) in.readValue((BestDay.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.dailyAverage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.daysIncludingHolidays = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.daysMinusHolidays = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.editors, (com.kondenko.pocketwaka.api.model.stats.Editor.class.getClassLoader()));
            instance.end = ((String) in.readValue((String.class.getClassLoader())));
            instance.holidays = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.humanReadableDailyAverage = ((String) in.readValue((String.class.getClassLoader())));
            instance.humanReadableTotal = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.isAlreadyUpdating = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.isStuck = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.isUpToDate = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            in.readList(instance.languages, (com.kondenko.pocketwaka.api.model.stats.Language.class.getClassLoader()));
            instance.modifiedAt = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.operatingSystems, (com.kondenko.pocketwaka.api.model.stats.OperatingSystem.class.getClassLoader()));
            instance.project = in.readValue((Object.class.getClassLoader()));
            in.readList(instance.projects, (com.kondenko.pocketwaka.api.model.stats.Project.class.getClassLoader()));
            instance.range = ((String) in.readValue((String.class.getClassLoader())));
            instance.start = ((String) in.readValue((String.class.getClassLoader())));
            instance.status = ((String) in.readValue((String.class.getClassLoader())));
            instance.timeout = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.timezone = ((String) in.readValue((String.class.getClassLoader())));
            instance.totalSeconds = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.userId = ((String) in.readValue((String.class.getClassLoader())));
            instance.username = ((String) in.readValue((String.class.getClassLoader())));
            instance.writesOnly = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public Stats[] newArray(int size) {
            return (new Stats[size]);
        }

    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(bestDay);
        dest.writeValue(createdAt);
        dest.writeValue(dailyAverage);
        dest.writeValue(daysIncludingHolidays);
        dest.writeValue(daysMinusHolidays);
        dest.writeList(editors);
        dest.writeValue(end);
        dest.writeValue(holidays);
        dest.writeValue(humanReadableDailyAverage);
        dest.writeValue(humanReadableTotal);
        dest.writeValue(id);
        dest.writeValue(isAlreadyUpdating);
        dest.writeValue(isStuck);
        dest.writeValue(isUpToDate);
        dest.writeList(languages);
        dest.writeValue(modifiedAt);
        dest.writeList(operatingSystems);
        dest.writeValue(project);
        dest.writeList(projects);
        dest.writeValue(range);
        dest.writeValue(start);
        dest.writeValue(status);
        dest.writeValue(timeout);
        dest.writeValue(timezone);
        dest.writeValue(totalSeconds);
        dest.writeValue(userId);
        dest.writeValue(username);
        dest.writeValue(writesOnly);
    }

    public int describeContents() {
        return 0;
    }

}
