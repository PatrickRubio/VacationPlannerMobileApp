package com.example.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    // Data Access Object
    // Inserts data into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    // Queries data into the database
    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionID ASC")
    LiveData<List<Excursion>> getAllExcursions();

    // Queries all excursions from the database with the vacationID
    @Query("SELECT * FROM EXCURSIONS WHERE vacationID = :vacation ORDER BY excursionID ASC")
    LiveData<List<Excursion>> getAssociatedExcursions(int vacation);

    @Query("SELECT * FROM EXCURSIONS WHERE vacationID = :vacationID")
    List<Excursion> getAssociatedExcursionsSync(int vacationID);

}
