package com.example.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {
    // Data Access Object
    // Inserts data into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    // Queries data into the database
    @Query("SELECT * FROM VACATIONS ORDER BY vacationID ASC")
    LiveData<List<Vacation>> getAllVacations();
}
