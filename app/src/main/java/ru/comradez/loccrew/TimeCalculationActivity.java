package ru.comradez.loccrew;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import kotlin.Pair;


public class TimeCalculationActivity extends AppCompatActivity {

    private final DBHelper dbHelper = new DBHelper(TimeCalculationActivity.this, DataBaseNameList.BUFFER, "TIME_CALC");
    private LocalDateTime selectedDateTime;
    private Button activeButton;
    private final DBRecordBuilder[] builders = new DBRecordBuilder[] {new DBRecordBuilder(1),
                                                                new DBRecordBuilder(2),
                                                                new DBRecordBuilder(3)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_calculation);

        initializeViews();
    }

    private void initializeViews() {
        Button[] jobStartButtons = new Button[]{
                findViewById(R.id.job_1_start_button),
                findViewById(R.id.job_2_start_button),
                findViewById(R.id.job_3_start_button)
        };

        setButtonClickListeners(jobStartButtons);

        Button[] jobFinishButtons = new Button[]{
                findViewById(R.id.job_1_finish_button),
                findViewById(R.id.job_2_finish_button),
                findViewById(R.id.job_3_finish_button)
        };

        setButtonClickListeners(jobFinishButtons);

        Button[] addJobButtons = new Button[]{
                findViewById(R.id.add_job_button_2),
                findViewById(R.id.add_job_button_3)
        };

        TextView[] jobLabels = new TextView[]{
                findViewById(R.id.job_1_text),
                findViewById(R.id.job_2_text),
                findViewById(R.id.job_3_text)
        };
    }

    private void setButtonClickListeners(Button[] buttons) {
        for (Button button : buttons) {
            button.setOnClickListener(v -> showDateTimePickerDialog(button));
        }
    }

    private void showDateTimePickerDialog(Button button) {
        final LocalDate currentDate = LocalDate.now();
        activeButton = button;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDateTime = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, 0, 0);
                    showTimePickerDialog();
                },
                currentDate.getYear(),
                currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth());

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final LocalTime currentTime = LocalTime.now();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime = selectedDateTime.withHour(hourOfDay).withMinute(minute);
                    saveResultOnBuffer();
                },
                currentTime.getHour(),
                currentTime.getMinute(),
                true);
        timePickerDialog.show();
    }

    private void saveResultOnBuffer() {
        if (selectedDateTime != null) {
            String formattedDateTime = selectedDateTime.format(DateTimeString.formatter);
            activeButton.setText(formattedDateTime);

            Pair<Integer, String> metaDataPair = getMetaDataFromActiveButton();
            for (DBRecordBuilder b : builders){
                if (b.respondForId(metaDataPair.getFirst()))
                    b.add(dbHelper,metaDataPair.getFirst(), formattedDateTime,metaDataPair.getSecond().equals("start"));
            }
        }
    }
    private Pair<Integer, String> getMetaDataFromActiveButton() {
        String sourceName = activeButton.getResources().getResourceName(activeButton.getId());
        String[] tempArray = sourceName.split("_");
        return new Pair<>(Integer.parseInt(tempArray[1]), tempArray[2]); //id кнопки всегда имеет формат job_ID_start/finish_button,
                                                                            // следовательно в итоговом архиве после разбиения строки нам нужны 1 и 2 индексы как искомые ID и назначение.
    }
}