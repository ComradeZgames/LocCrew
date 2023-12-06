package ru.comradez.loccrew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    private Button[] jobStartButtons;
    private Button[] jobFinishButtons;
    private final DBRecordBuilder[] builders = new DBRecordBuilder[]{new DBRecordBuilder(1),
            new DBRecordBuilder(2),
            new DBRecordBuilder(3)};

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_calculation);

        initializeViews();
        if (!dbHelper.getAll().isEmpty())
            showStartingDialog();
    }

    private void showStartingDialog() {
        AlertDialog.Builder dilogBuilder = new AlertDialog.Builder(TimeCalculationActivity.this);
        dilogBuilder.setTitle(R.string.TC_dialog_title);
        dilogBuilder.setMessage(R.string.TC_dialog_message);
        dilogBuilder.setPositiveButton(R.string.TC_dialog_posButton, (dialog, which) -> {
            for (DBRecordBuilder builder : builders) {
                builder.recordBuilderLoad(dbHelper);
            }
            for (int i = 0; i < builders.length; i++) {
                jobStartButtons[i].setText(builders[i].getStart());
                jobFinishButtons[i].setText(builders[i].getFinish());
            }
        });
        dilogBuilder.setNegativeButton(R.string.TC_dilog_negButton, (dialog, which) -> dbHelper.clearAll());
        AlertDialog startingDialog = dilogBuilder.create();
        startingDialog.setCancelable(false);
        startingDialog.setCanceledOnTouchOutside(false);
        startingDialog.show();
    }

    private void initializeViews() {
        jobStartButtons = new Button[]{
                findViewById(R.id.job_1_start_button),
                findViewById(R.id.job_2_start_button),
                findViewById(R.id.job_3_start_button)
        };

        setButtonClickListeners(jobStartButtons);

        jobFinishButtons = new Button[]{
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
            showButtonText(formattedDateTime);

            Pair<Integer, String> metaDataPair = getMetaDataFromActiveButton();
            for (DBRecordBuilder b : builders) {
                if (b.respondForId(metaDataPair.getFirst()))
                    b.add(dbHelper, metaDataPair.getFirst(), formattedDateTime, metaDataPair.getSecond().equals("start"));
            }
        }
    }

    private void showButtonText(String formattedDateTime) {
        activeButton.setText(formattedDateTime);
    }

    private Pair<Integer, String> getMetaDataFromActiveButton() {
        String sourceName = activeButton.getResources().getResourceName(activeButton.getId());
        String[] tempArray = sourceName.split("_");
        return new Pair<>(Integer.parseInt(tempArray[1]), tempArray[2]); //id кнопки всегда имеет формат job_ID_start/finish_button,
        // следовательно в итоговом архиве после разбиения строки нам нужны 1 и 2 индексы как искомые ID и назначение.
    }
}