package com.example.d308.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.d308.entities.Vacation;
import com.example.d308.entities.VacationWithExcursions;

import java.util.List;

@Dao
public interface VacationDao {
    @Query("SELECT * FROM vacation")
    List<Vacation> getAll();

    @Transaction
    @Query("SELECT * FROM vacation")
    List<VacationWithExcursions> getAllWithExcursions();

    @Insert
    void insertAll(Vacation... vacations);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);
}

