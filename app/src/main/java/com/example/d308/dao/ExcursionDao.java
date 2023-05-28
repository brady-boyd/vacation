package com.example.d308.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.d308.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDao {
    @Query("SELECT * FROM excursion")
    List<Excursion> getAll();

    @Insert
    void insertAll(Excursion... excursions);

    @Delete
    void delete(Excursion excursion);
}
