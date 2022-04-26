package com.example.pollingtest.chores;

import android.os.Parcel;
import android.os.Parcelable;

public class Chore implements Parcelable {
    private String name;
    private int priority;
    private String date;

    //Constructors
    public Chore() {
    }

    public Chore(String name, int priority, String date) {
        this.name = name;
        this.priority = priority;
        this.date = date;
    }

    protected Chore(Parcel in) {
        name = in.readString();
        priority = in.readInt();
        date = in.readString();
    }

    public static final Creator<Chore> CREATOR = new Creator<Chore>() {
        @Override
        public Chore createFromParcel(Parcel in) {
            return new Chore(in);
        }

        @Override
        public Chore[] newArray(int size) {
            return new Chore[size];
        }
    };

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(priority);
        parcel.writeString(date);
    }
}
