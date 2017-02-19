
package com.kondenko.pocketwaka.api.model.stats;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kondenko.pocketwaka.R;

import java.util.concurrent.TimeUnit;

public class BestDay implements Parcelable
{

    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("modified_at")
    @Expose
    public Object modifiedAt;
    @SerializedName("total_seconds")
    @Expose
    public Integer totalSeconds;

    public String getHumanReadableTime(Context context) {
        String pattern = context.getString(R.string.stats_time_format);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - TimeUnit.HOURS.toMinutes(hours);
        return String.format(pattern, hours, minutes);
    }


    public final static Parcelable.Creator<BestDay> CREATOR = new Creator<BestDay>() {

        @SuppressWarnings({
            "unchecked"
        })
        public BestDay createFromParcel(Parcel in) {
            BestDay instance = new BestDay();
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.date = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.modifiedAt = in.readValue((Object.class.getClassLoader()));
            instance.totalSeconds = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public BestDay[] newArray(int size) {
            return (new BestDay[size]);
        }

    }
    ;

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(createdAt);
        dest.writeValue(date);
        dest.writeValue(id);
        dest.writeValue(modifiedAt);
        dest.writeValue(totalSeconds);
    }

    public int describeContents() {
        return  0;
    }

}
