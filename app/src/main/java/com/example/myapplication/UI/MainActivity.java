package com.example.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int numAlert;

    // Adapter and List for search bar
    private Repository repository;
    private VacationAdapter vacationAdapter;
    private List<Vacation> allVacations = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new Repository(getApplication());

        // Initialize RecyclerView using class-level variable (no local variable)
        recyclerView = findViewById(R.id.recyclerView);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.GONE); // Hide initially

        // Start with empty list
        vacationAdapter.setVacations(new ArrayList<>());

        // LiveData observer
        repository.getAllVacations().observe(this, vacations -> allVacations = vacations);

        // SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(true);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVacations(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVacations(newText);
                return true;
            }
        });

        // Button setup for Main Activity Screen
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VacationList.class);
            intent.putExtra("test", "Information Sent");
            startActivity(intent);
        });
    }

    private void filterVacations(String query) {
        if (allVacations == null || recyclerView == null) return;

        List<Vacation> filtered = new ArrayList<>();
        for (Vacation v : allVacations) {
            if (v.getVacationName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(v);
            }
        }
        vacationAdapter.setVacations(filtered);

        // Show RecyclerView only if there's a query
        if (!query.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }
}
