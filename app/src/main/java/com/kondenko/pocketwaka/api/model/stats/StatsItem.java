package com.kondenko.pocketwaka.api.model.stats;

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
public class StatsItem  implements Parcelable {

    public static final int DEFAULT_COLOR_VAL = -1;

    @SerializedName("digital")
    @Expose
    public String digital;
    @SerializedName("hours")
    @Expose
    public Integer hours;
    @SerializedName("minutes")
    @Expose
    public Integer minutes;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("percent")
    @Expose
    public Double percent;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("total_seconds")
    @Expose
    public Integer totalSeconds;

    public int color = DEFAULT_COLOR_VAL;

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

    }
            ;

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
        return  0;
    }

}
