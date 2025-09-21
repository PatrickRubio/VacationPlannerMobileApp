package com.example.myapplication.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.dao.ExcursionDAO;
import com.example.myapplication.dao.VacationDAO;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private ExcursionDAO mExcursionDAO;
    private VacationDAO mVacationDAO;

    private List<Vacation> mALLVacations;
    private List<Excursion> mALLExcursions;

    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mVacationDAO = db.vacationDAO();
    }

    // Gets vacations from database and creates a new database
    // -------------VACATION METHODS--------------
    // Expose LiveData for Vacations
    public LiveData<List<Vacation>> getAllVacations() {
        return mVacationDAO.getAllVacations();
    }
    // Insert, update, delete asynchronous
    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.update(vacation));
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.delete(vacation));
    }

    // -------------EXCURSION METHODS--------------
    public LiveData<List<Excursion>> getAllExcursions() {
        return mExcursionDAO.getAllExcursions();
    }

    // Return LiveData for excursions associated with a specific vacation
    public LiveData<List<Excursion>> getAssociatedExcursions(int vacationID) {
        return mExcursionDAO.getAssociatedExcursions(vacationID);
    }

    // Insert, update, delete excursions asynchronously
    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.insert(excursion));
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }
}
