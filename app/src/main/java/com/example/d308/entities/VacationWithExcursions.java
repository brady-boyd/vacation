package com.example.d308.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class VacationWithExcursions {
    @Embedded public Vacation vacation;
    @Relation(
            parentColumn = "id",
            entityColumn = "vacationId"
    )
    public List<Excursion> excursions;
}
