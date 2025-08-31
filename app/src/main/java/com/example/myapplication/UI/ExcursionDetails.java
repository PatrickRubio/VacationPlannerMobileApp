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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        // Declaring Variables to be able to edit and get information to layout widget
        repository = new Repository(getApplication());
        name = getIntent().getStringExtra("name");
        editName = findViewById(R.id.excursionName);
        editName.setText(name);
        price = getIntent().getDoubleExtra("price", -1.0);
        editPrice = findViewById(R.id.excursionPrice);
        editPrice.setText(Double.toString(price));

        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        excursionDate = getIntent().getStringExtra("date");
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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
                new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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
        Spinner spinner=findViewById(R.id.spinner);
        ArrayList<Vacation> vacationArrayList=new ArrayList<>();

        vacationArrayList.addAll(repository.getmAllVacations());

        ArrayAdapter<Vacation>vacationAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,vacationArrayList);
        spinner.setAdapter(vacationAdapter);
        spinner.setSelection(0);
    }
    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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
        if (item.getItemId() == R.id.excursionsave) {
            Excursion excursion;
            String excursionDate = editDate.getText().toString();
            if (excursionID == -1) {
                if (repository.getmALLExcursions().size() == 0)
                    excursionID = 1;
                else
                    excursionID = repository.getmALLExcursions().get(repository.getmALLExcursions().size() - 1).getExcursionID() + 1;
                excursion = new Excursion(excursionID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationID, excursionDate);
                repository.insert(excursion);
            } else {
                excursion = new Excursion(excursionID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationID, excursionDate);
                repository.update(excursion);
            }
            return true;
        }
        if (item.getItemId() == R.id.share) {
            Intent sentIntent= new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString()+ "EXTRA_TEXT");
            sentIntent.putExtra(Intent.EXTRA_TITLE, editNote.getText().toString()+ "EXTRA_TITLE");
            sentIntent.setType("text/plain");
            Intent shareIntent=Intent.createChooser(sentIntent,null);
            startActivity(shareIntent);
            return true;
        }
//        if (item.getItemId() == R.id.notify) {
//            String dateFromScreen = editDate.getText().toString();
//            String myFormat = "MM/dd/yy";
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            Date myDate = null;
//            try {
//                myDate = sdf.parse(dateFromScreen);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Long trigger = myDate.getTime();
//            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
//            intent.putExtra("key", "Correct!");
//            PendingIntent sender=PendingIntent.getBroadcast(ExcursionDetails.this,++MainActivity.numAlert, intent,PendingIntent.FLAG_IMMUTABLE);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger,sender);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}