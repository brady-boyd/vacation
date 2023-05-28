package com.example.d308.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.d308.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDao {
    @Query("SELECT * FROM vacation")
    List<Vacation> getAll();

    @Insert
    void insertAll(Vacation... vacations);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);
}
