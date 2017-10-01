package com.kondenko.pocketwaka.data.stats.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The parent class for Editors, Languages etc.
 * Since all these classes contain the same fields,
 * this class is needed to reduce code duplication
 * while providing classes with different names
 * derived from the JSON object.
 *
 * @see com.kondenko.pocketwaka.ui.CardStats
 * @see com.kondenko.pocketwaka.ui.CardStatsListAdapter
 */
public class StatsItem implements Parcelable {

    @SerializedName("digital")
    @Expose
    public String digital = null;
    @SerializedName("hours")
    @Expose
    public Integer hours = null;
    @SerializedName("minutes")
    @Expose
    public Integer minutes = null;
    @SerializedName("name")
    @Expose
    public String name = null;
    @SerializedName("percent")
    @Expose
    public Double percent = null;
    @SerializedName("text")
    @Expose
    public String text = null;
    @SerializedName("total_seconds")
    @Expose
    public Integer totalSeconds = null;

    public int color;

    public final static Parcelable.Creator<Editor> CREATOR = new Creator<Editor>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Editor createFromParcel(Parcel in) {
            Editor instance = new Editor();
            instance.digital = ((String) in.readValue((String.class.getClassLoader())));
            instance.hours = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.minutes = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.percent = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.text = ((String) in.readValue((String.class.getClassLoader())));
            instance.totalSeconds = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Editor[] newArray(int size) {
            return (new Editor[size]);
        }

    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(digital);
        dest.writeValue(hours);
        dest.writeValue(minutes);
        dest.writeValue(name);
        dest.writeValue(percent);
        dest.writeValue(text);
        dest.writeValue(totalSeconds);
    }

    public int describeContents() {
        return 0;
    }

}
