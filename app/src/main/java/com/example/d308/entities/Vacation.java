package com.example.d308.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Vacation implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String hotel;
    private String startDate;
    private String endDate;
    // Add other properties as needed

    public Vacation() {
        // Empty constructor required by Room
    }

    protected Vacation(Parcel in) {
        id = in.readInt();
        title = in.readString();
        hotel = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    public static final Creator<Vacation> CREATOR = new Creator<Vacation>() {
        @Override
        public Vacation createFromParcel(Parcel in) {
            return new Vacation(in);
        }

        @Override
        public Vacation[] newArray(int size) {
            return new Vacation[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Add other getters and setters as needed

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(hotel);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }
}
