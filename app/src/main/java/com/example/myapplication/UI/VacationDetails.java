package com.example.myapplication.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.ViewTreeOnBackPressedDispatcherOwner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String name;
    String vacationHotel;
    String startDate;
    String endDate;
    double price;
    int vacationID;

    EditText editName;
    EditText editPrice;
    EditText editHotel;
    TextView editStartDate;
    TextView editEndDate;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;

    DatePickerDialog.OnDateSetListener setStartDate;
    DatePickerDialog.OnDateSetListener setEndDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);

        editName = findViewById(R.id.titletext);
        editPrice = findViewById(R.id.pricetext);
        editHotel = findViewById(R.id.hoteltext);
        editStartDate = findViewById(R.id.startDate);
        editEndDate = findViewById(R.id.endDate);
        vacationID = getIntent().getIntExtra("id", -1);
        name = getIntent().getStringExtra("name");
        price = getIntent().getDoubleExtra("price", 0.0);
        vacationHotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        editName.setText(name);
        editHotel.setText(vacationHotel);
        editPrice.setText(Double.toString(price));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationID", vacationID);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getmALLExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        // Date Information to see if existing dates are available

        if (startDate != null && !startDate.isEmpty()) {
            try {
                Date parsedStartDate = sdf.parse(startDate);
                myCalendarStart.setTime(parsedStartDate);
                updateStartDateLabel();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                Date parsedEndDate = sdf.parse(endDate);
                myCalendarEnd.setTime(parsedEndDate);
                updateEndDateLabel();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Date Pickers for start and end dates
        setStartDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDateLabel();
            }
        };

        setEndDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDateLabel();
            }
        };
        // Date click listeners for start and end dates
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VacationDetails.this, setStartDate,
                        myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VacationDetails.this, setEndDate,
                        myCalendarEnd.get(Calendar.YEAR),
                        myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }
    // Start and end date labels
    private void updateStartDateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    // Helper method to update end date label
    private void updateEndDateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }
    // Menu inflater for vacation details
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;

            try {
                String vacationHotel = editHotel.getText().toString();
                String startDate = editStartDate.getText().toString();
                String endDate = editEndDate.getText().toString();
                if (vacationID == -1) {
                    if (repository.getmAllVacations().size() == 0) vacationID = 1;
                    else
                        vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                    vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationHotel, startDate, endDate);
                    repository.insert(vacation);
                    this.finish();
                } else {
                    vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationHotel, startDate, endDate);
                    repository.update(vacation);
                    this.finish();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for the price.", Toast.LENGTH_LONG).show();
                return true;

            }
        }
        if (item.getItemId() == R.id.vacationdelete) {
            for (Vacation vacation:repository.getmAllVacations()) {
                if(vacation.getVacationID() == vacationID)currentVacation = vacation;
            }
            numExcursions=0;
            for (Excursion excursion: repository.getmALLExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }
            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getmALLExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }
}