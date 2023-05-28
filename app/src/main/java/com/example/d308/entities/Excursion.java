package com.example.d308.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    // Other fields...

    public Excursion(int id, String title /*, other parameters...*/) {
        this.id = id;
        this.title = title;
        // Initialize other fields...
    }

    // Getter and setter methods...

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

    // Other getter and setter methods...
}

