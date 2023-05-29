package com.example.d308.entities;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Vacation.class,
        parentColumns = "id",
        childColumns = "vacationId",
        onDelete = CASCADE))
public class Excursion implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int vacationId;

    // Other fields...

    public Excursion() {
        // Empty constructor required by Room
    }

    protected Excursion(Parcel in) {
        id = in.readInt();
        title = in.readString();
        // Read other fields from Parcel...
    }

    public static final Parcelable.Creator<Excursion> CREATOR = new Parcelable.Creator<Excursion>() {
        @Override
        public Excursion createFromParcel(Parcel in) {
            return new Excursion(in);
        }

        @Override
        public Excursion[] newArray(int size) {
            return new Excursion[size];
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

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }

    // Other getter and setter methods...

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        // Write other fields to Parcel...
    }
}
