
package com.kondenko.pocketwaka.api.model.stats;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataWrapper implements Parcelable
{

    @SerializedName("data")
    @Expose
    public Stats stats;

    public final static Parcelable.Creator<DataWrapper> CREATOR = new Creator<DataWrapper>() {
        @SuppressWarnings({
            "unchecked"
        })
        public DataWrapper createFromParcel(Parcel in) {
            DataWrapper instance = new DataWrapper();
            instance.stats = ((Stats) in.readValue((Stats.class.getClassLoader())));
            return instance;
        }
        public DataWrapper[] newArray(int size) {
            return (new DataWrapper[size]);
        }

    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(stats);
    }

    public int describeContents() {
        return  0;
    }

}
