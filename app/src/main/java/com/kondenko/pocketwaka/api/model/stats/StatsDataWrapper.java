
package com.kondenko.pocketwaka.api.model.stats;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatsDataWrapper implements Parcelable {

    @SerializedName("data")
    @Expose
    public Stats stats = null;

    public final static Parcelable.Creator<StatsDataWrapper> CREATOR = new Creator<StatsDataWrapper>() {
        @SuppressWarnings({
                "unchecked"
        })
        public StatsDataWrapper createFromParcel(Parcel in) {
            StatsDataWrapper instance = new StatsDataWrapper();
            instance.stats = ((Stats) in.readValue((Stats.class.getClassLoader())));
            return instance;
        }

        public StatsDataWrapper[] newArray(int size) {
            return (new StatsDataWrapper[size]);
        }

    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(stats);
    }

    public int describeContents() {
        return 0;
    }

}
