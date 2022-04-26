package com.example.pollingtest.chores;

import android.os.Parcel;
import android.os.Parcelable;

public class ChoreWithID extends Chore {

    private String id;

    public ChoreWithID(String name, int priority, String date, String id) {
        super(name, priority, date);
        this.id = id;
    }

    protected ChoreWithID(Parcel in) {
        super(in);
        id = in.readString();
    }

    public static final Parcelable.Creator<ChoreWithID> CREATOR = new Parcelable.Creator<ChoreWithID>() {
        @Override
        public ChoreWithID createFromParcel(Parcel in) {
            return new ChoreWithID(in);
        }

        @Override
        public ChoreWithID[] newArray(int size) {
            return new ChoreWithID[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(id);
    }
}
