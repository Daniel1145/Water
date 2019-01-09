package com.example.water;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Plant implements Parcelable {
    private String mName;
    private String mSpecies;
    private int mWaterSchedule;
    private int daysUntilWater;

    public Plant(String name, int waterSchedule) {
        mName = name;
        mWaterSchedule = waterSchedule;
        daysUntilWater = waterSchedule + 1;
    }

    public String getName() {
        return mName;
    }

    public int getWaterSchedule(){
        return mWaterSchedule;
    }

    public int getDaysUntilWater() { return daysUntilWater;}

    public void setName(String name) {mName = name;}

    public void setDaysUntilWater(int days) {daysUntilWater = days;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mSpecies);
        dest.writeInt(this.mWaterSchedule);
        dest.writeInt(this.daysUntilWater);
    }

    protected Plant(Parcel in) {
        this.mName = in.readString();
        this.mSpecies = in.readString();
        this.mWaterSchedule = in.readInt();
        this.daysUntilWater = in.readInt();
    }

    public static final Creator<Plant> CREATOR = new Creator<Plant>() {
        @Override
        public Plant createFromParcel(Parcel source) {
            return new Plant(source);
        }

        @Override
        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };
}
