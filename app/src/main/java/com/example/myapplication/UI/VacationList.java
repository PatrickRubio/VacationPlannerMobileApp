package com.example.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        FloatingActionButton fab=findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });
        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // EdgeInsets padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Repository
        repository = new Repository(getApplication());

        // Observe LiveData for all vacations
        repository.getAllVacations().observe(this, vacations -> {
            // This updates the adapter automatically whenever the database changes
            vacationAdapter.setVacations(vacations);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        List<Vacation> allVacations = repository.getmAllVacations();
//        RecyclerView recyclerView = findViewById((R.id.recyclerView));
//        final VacationAdapter vacationAdapter = new VacationAdapter(this);
//        recyclerView.setAdapter(vacationAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        vacationAdapter.setVacations(allVacations);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId()==R.id.mysample) {
            repository = new Repository(getApplication());
            // Sample vacations
            Vacation vacation = new Vacation(0, "Holiday Trip", 1000.0, "Disney Hotel", "12/08/25", "12/15/25");
            repository.insert(vacation);
            vacation = new Vacation(0, "Honeymoon", 2000.0, "One Hotel", "10/08/25", "10/15/25");
            repository.insert(vacation);
            // Excursions for each sample vacation
            Excursion excursion = new Excursion(0, "Beach", 15, 1, "10/08/25");
            repository.insert(excursion);
            excursion = new Excursion(0, "Surfing", 15, 1, "12/08/25");
            repository.insert(excursion);
            excursion = new Excursion(0, "Hiking", 50, 2, "10/10/25");
            repository.insert(excursion);
            excursion = new Excursion(0, "Dinner", 75, 2, "10/12/25");
            repository.insert(excursion);

            return true;
        }
        return true;
    }
}