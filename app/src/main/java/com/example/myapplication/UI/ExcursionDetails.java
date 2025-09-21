package com.example.myapplication.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    // Create variables for Excursion Details page
    String name;
    Double price;
    String excursionDate;
    int excursionID;
    int vacationID;
    EditText editName;
    EditText editPrice;
    EditText editNote;
    TextView editDate;
    Repository repository;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();

    // Snapshots of LiveData for synchronous access
    private List<Vacation> vacationSnapshot = new ArrayList<>();
    private List<Excursion> excursionSnapshot = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        // Declaring Variables to be able to edit and get information to layout widget
        repository = new Repository(getApplication());

        // Get intent extras
        name = getIntent().getStringExtra("name");
        price = getIntent().getDoubleExtra("price", -1.0);
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        excursionDate = getIntent().getStringExtra("date");

        // Initialize UI elements
        editName = findViewById(R.id.excursionName);
        editPrice = findViewById(R.id.excursionPrice);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);
        Spinner spinner = findViewById(R.id.spinner);

        editName.setText(name);
        editPrice.setText(Double.toString(price));

        if (excursionDate != null) {
            editDate.setText(excursionDate);
        }

        // Date picker
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info=editDate.getText().toString();
                if(info.equals(""))info="08/30/25";
                try{
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, startDate,
                        myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startDate=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        // Created Spinner which populates it inside the observer
        repository.getAllVacations().observe(this, vacations -> {
            ArrayAdapter<Vacation> vacationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacations);
            vacationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(vacationAdapter);

            // Set spinner to the current vacationID
            for (int i = 0; i < vacations.size(); i++) {
                if (vacations.get(i).getVacationID() == vacationID) {
                    spinner.setSelection(i);
                    break;
                }
            }
        });
        // Observe excursions to keep snapshot for ID generation
        repository.getAllExcursions().observe(this, excursions -> excursionSnapshot = excursions);
    }
    private void updateLabelStart() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        // Save the excursion with the menu
        if (item.getItemId() == R.id.excursionsave) {
            String excursionDate = editDate.getText().toString();
            // Validation for excursion date being within vacation range
            Vacation vacation = null;
            for (Vacation vac : vacationSnapshot) {
                if (vac.getVacationID() == vacationID) {
                    vacation = vac;
                    break;
                }
            }
            if (vacation != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                try {
                    Date excDate = sdf.parse(excursionDate);
                    Date startDate = sdf.parse(vacation.getStartDate());
                    Date endDate = sdf.parse(vacation.getEndDate());

                    if (excDate.before(startDate) || excDate.after(endDate)) {
                        Toast.makeText(this, "The excursion date must be within range of vacation's START and END dates", Toast.LENGTH_LONG).show();
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Excursion date format not valid.", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Excursion excursion;
            if (excursionID == -1) {
                // Generate new ID
                excursionID = excursionSnapshot.isEmpty() ? 1 :
                        excursionSnapshot.get(excursionSnapshot.size() - 1).getExcursionID() + 1;
            }

            excursion = new Excursion(excursionID, editName.getText().toString(),
                    Double.parseDouble(editPrice.getText().toString()),
                    vacationID, excursionDate);

            if (getIntent().getIntExtra("id", -1) == -1)
                repository.insert(excursion);
            else
                repository.update(excursion);
            finish();
            return true;
        }
        // Delete excursion with the menu
        if (item.getItemId() == R.id.excursiondelete) {
            // See which excursion to delete
            Excursion excursion = new Excursion(excursionID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationID, editDate.getText().toString());
            // Call the repository to delete excursion
            repository.delete(excursion);
            // Close the screen
            this.finish();
            return true;

        }

        // Share the excursion with the menu
        if (item.getItemId() == R.id.share) {
            String excursionDetailsShare = "Excursion name: " + editName.getText().toString() + "\n"
                    + "Price: $" + editPrice.getText().toString() + "\n"
                    + "Note: " + editNote.getText().toString() + "\n"
                    + "Date: " + editDate.getText().toString();
            Intent sentIntent= new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, excursionDetailsShare);
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Excursion Details");
            sentIntent.setType("text/plain");
            Intent shareIntent=Intent.createChooser(sentIntent,null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String dateFromScreen = editDate.getText().toString();
            String dateFormat = "MM/dd/yy";
            String notifyAlert = "Don't forget about your " + name + " excursion today!";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long trigger = myDate.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("key", notifyAlert);
            PendingIntent sender=PendingIntent.getBroadcast(ExcursionDetails.this,++MainActivity.numAlert, intent,PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger,sender);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}