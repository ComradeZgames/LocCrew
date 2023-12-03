package ru.comradez.loccrew;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TimeCalculationActivity extends AppCompatActivity {

    private Button[] jobStartButtons;
    private Button[] jobFinishButtons;
    private Button[] addJobButtons;
    private TextView[] jobLabels;
    private DBHelper dbHelper = new DBHelper(TimeCalculationActivity.this);;
    private Calendar dateAndTime = Calendar.getInstance();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yy\nHH:mm");

    private Button activeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_calculation);

        initializeViews();
    }

    private void initializeViews() {
        jobStartButtons = new Button[]{
                findViewById(R.id.job_1_start_button),
                findViewById(R.id.job_2_start_button),
                findViewById(R.id.job_3_start_button)
        };

        jobFinishButtons = new Button[]{
                findViewById(R.id.job_1_finish_button),
                findViewById(R.id.job_2_finish_button),
                findViewById(R.id.job_3_finish_button)
        };

        addJobButtons = new Button[]{
                findViewById(R.id.add_job_button_2),
                findViewById(R.id.add_job_button_3)
        };

        jobLabels = new TextView[]{
                findViewById(R.id.job_1_text),
                findViewById(R.id.job_2_text),
                findViewById(R.id.job_3_text)
        };
    }

    public void setDate(View v) {
        new DatePickerDialog(TimeCalculationActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
        activeButton = (Button) v;
    }

    public void setTime(View v) {
        new TimePickerDialog(TimeCalculationActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);

        dbHelper.AddRecord(getDateTimeOnButton(activeButton));
    };

    private DateTimeString getDateTimeOnButton(Button activeButton) {
      String sourceName = activeButton.getResources().getResourceName(activeButton.getId());
      String[] tempArray = sourceName.split("_");
      if (tempArray[2].equals("start"))
      return new DateTimeString(Integer.parseInt(tempArray[1]), dateAndTime.getTimeInMillis(), true);
      else return new DateTimeString(Integer.parseInt(tempArray[1]), dateAndTime.getTimeInMillis(), false);
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        setTime(view);
    };
}