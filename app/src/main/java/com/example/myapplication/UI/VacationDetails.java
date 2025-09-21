package com.example.myapplication.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
    String dateFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    Random rand = new Random();
    int numAlert = rand.nextInt(99999);

    // Snapshots for synchronous access
    private List<Excursion> excursionSnapshot = new ArrayList<>();
    private List<Vacation> vacationSnapshot = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);

        // Initialize UI
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

        numAlert = rand.nextInt(99999);

        // LiveData observers
        repository = new Repository(getApplication());

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // LiveData observers
        repository.getAllVacations().observe(this, vacations -> vacationSnapshot = vacations);

        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> {
            excursionSnapshot = excursions;
            excursionAdapter.setExcursions(excursions); // just update data
        });


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
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    // Helper method to update end date label
    private void updateEndDateLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }

    // Menu inflater for vacation details
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        // Save Vacation
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;

            try {
                String vacationHotel = editHotel.getText().toString();
                String startDateStr = editStartDate.getText().toString();
                String endDateStr = editEndDate.getText().toString();

                // Validation to ensure end date is after start date for the user
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                Date start = sdf.parse(startDateStr);
                Date end = sdf.parse(endDateStr);
                if (end.before(start)) {
                    Toast.makeText(this, "The end date must be after the start date", Toast.LENGTH_LONG).show();
                    return true;
                }
                // If user vacation does not exist already
                if (vacationID == -1) {
                    vacationID = vacationSnapshot.isEmpty() ?
                            1 : vacationSnapshot.get(vacationSnapshot.size() - 1).getVacationID() + 1;
                    vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationHotel, startDateStr, endDateStr);
                    repository.insert(vacation);
                    this.finish();
                } else {
                    vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationHotel, startDateStr, endDateStr);
                    repository.update(vacation);
                    this.finish();
                }

            } catch (ParseException e) {
                Toast.makeText(this, "Please enter dates in the correct format.", Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for the price.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        // If the user selects the option to delete a vacation
        if (item.getItemId() == R.id.vacationdelete) {
            for (Vacation v : vacationSnapshot) {
                if (v.getVacationID() == vacationID) currentVacation = v;
            }
            numExcursions = 0;
            for (Excursion e : excursionSnapshot) {
                if (e.getVacationID() == vacationID) numExcursions++;
            }
            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        // Alert for start date
        if (item.getItemId() == R.id.alertstart) {
            String startOnScreenDate = editStartDate.getText().toString();
            String mainAlert = "Vacation " + name + " is about to start";
            mainAlertPicker(startOnScreenDate, mainAlert);
            return true;
        }

        // Alert for end date
        if (item.getItemId() == R.id.alertend) {
            // Alert for end date
            String endOnScreenDate = editEndDate.getText().toString();
            String mainAlert = "Vacation " + name + " is about to end";
            mainAlertPicker(endOnScreenDate, mainAlert);
            return true;
        }

        // Alert for both start and end date
        if (item.getItemId() == R.id.alertboth) {
            // Alert for start date
            String onScreenDate = editStartDate.getText().toString();
            String mainAlert = "Vacation " + name + " is starting soon";
            mainAlertPicker(onScreenDate, mainAlert);

            // Alert for end date
            onScreenDate = editEndDate.getText().toString();
            mainAlert = "Vacation " + name + " is about to end";
            mainAlertPicker(onScreenDate, mainAlert);
            return true;

        }
        // Enable sharing features with vacation details
        if (item.getItemId() == R.id.share) {
            // Vacation details
            String vacationDetailsShare = "Vacation name: " + editName.getText().toString() + "\n"
                    + "Hotel: " + editHotel.getText().toString() + "\n"
                    + "Price: $" + editPrice.getText().toString() + "\n"
                    + "Start Date: " + editStartDate.getText().toString() + "\n"
                    + "End Date: " + editEndDate.getText().toString();


            if (!excursionSnapshot.isEmpty()) {
                vacationDetailsShare += "\n\nExcursions:\n";
                for (Excursion e : excursionSnapshot) {
                    vacationDetailsShare += e.getExcursionName() + " on " + e.getExcursionDate() + "\n";
                }
            } else {
                vacationDetailsShare += "\nNo excursions planned.";
            }

            // Share intent
            Intent sentIntent= new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT,vacationDetailsShare);
            sentIntent.putExtra(Intent.EXTRA_TITLE,"Vacation to be shared");
            sentIntent.setType("text/plain");
            // Lets user choose where to share
            Intent shareIntent=Intent.createChooser(sentIntent,"Share vacation via");
            startActivity(shareIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Set the date alarm method with mainAlertPicker
    public void mainAlertPicker(String onScreenDate, String mainAlert) {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        Date date;
        try {
            date = sdf.parse(onScreenDate);
            if (date == null) return;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not Valid.", Toast.LENGTH_LONG).show();
            return;
        }

        long triggerTime = date.getTime();

        Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
        intent.putExtra("key", mainAlert);

        numAlert = rand.nextInt(99999);
        PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, sender);

        System.out.println("Vacation = " + numAlert);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
//        repository = new Repository(getApplication());
//        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
//        recyclerView.setAdapter(excursionAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        List<Excursion> filteredExcursions = new ArrayList<>();
//        for (Excursion e : repository.getmALLExcursions()) {
//            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
//        }
//        excursionAdapter.setExcursions(filteredExcursions);
//    }
}