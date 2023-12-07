package ru.comradez.loccrew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import kotlin.Pair;


public class TimeCalculationActivity extends AppCompatActivity implements BuilderObserver {

    private final DBHelper dbHelper = new DBHelper(TimeCalculationActivity.this, DataBaseNameList.BUFFER, "TIME_CALC");
    private LocalDateTime selectedDateTime;

    private Button activeButton;
    private Button[] jobStartButtons, jobFinishButtons, addJobButtons;
    private TextView[] jobLabels, restTexts;
    private TextView homeRestText;

    private final DBRecordBuilder[] builders = new DBRecordBuilder[]{new DBRecordBuilder(1),
            new DBRecordBuilder(2),
            new DBRecordBuilder(3)};
    private TimeCalculator timeCalculator = new TimeCalculator(dbHelper);

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        activityStateLoading();
    }

    private void activityStateLoading() {
        LoadBuildersRecords();

        if (dbHelper.getAll().isEmpty()) {
            return;
        }
        updateButtonLabels();
    }

    private void LoadBuildersRecords() {
        for (int i = 0; i < builders.length; i++) {
            if (builders[i].load(dbHelper)) {
                if (i != 0)
                    updateJobFieldsVisibility(addJobButtons[i - 1], (i - 1));
            }
        }
    }

    private void updateButtonLabels() {
        for (int i = 0; i < builders.length; i++) {
            updateButtonText(jobStartButtons[i], builders[i].getStart(), R.string.TC_start_button);
            updateButtonText(jobFinishButtons[i], builders[i].getFinish(), R.string.TC_finish_button);
        }
    }

    private void updateButtonText(Button button, String text, int defaultResourceId) {
        if (text != null) {
            button.setText(text);
        } else {
            button.setText(defaultResourceId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_calculation);

        initializeViews();
        if (!dbHelper.getAll().isEmpty())
            showStartingDialog();
    }

    private void showStartingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TimeCalculationActivity.this);
        dialogBuilder.setTitle(R.string.TC_dialog_title);
        dialogBuilder.setMessage(R.string.TC_dialog_message);
        dialogBuilder.setPositiveButton(R.string.TC_dialog_posButton, (dialog, which) -> {
            activityStateLoading();
        });
        dialogBuilder.setNegativeButton(R.string.TC_dialog_negButton, (dialog, which) -> dbHelper.clearAll());
        AlertDialog startingDialog = dialogBuilder.create();
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

        setTimeButtonClickListeners(jobStartButtons);

        jobFinishButtons = new Button[]{
                findViewById(R.id.job_1_finish_button),
                findViewById(R.id.job_2_finish_button),
                findViewById(R.id.job_3_finish_button)
        };

        setTimeButtonClickListeners(jobFinishButtons);

        addJobButtons = new Button[]{
                findViewById(R.id.add_job_button_2),
                findViewById(R.id.add_job_button_3)
        };

        setAddButtonClickListeners(addJobButtons);

        jobLabels = new TextView[]{
                findViewById(R.id.job_1_text),
                findViewById(R.id.job_2_text),
                findViewById(R.id.job_3_text)
        };

        restTexts = new TextView[]{
                findViewById(R.id.rest_text_1),
                findViewById(R.id.rest_text_2)
        };

        homeRestText = findViewById(R.id.home_rest_text);

        for (DBRecordBuilder builder : builders) {
            builder.addObserver(this);
        }
    }

    private void setTimeButtonClickListeners(Button[] buttons) {
        for (Button button : buttons) {
            button.setOnClickListener(v -> showDateTimePickerDialog(button));
        }
    }

    private void setAddButtonClickListeners(Button[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> {
                updateJobFieldsVisibility(buttons[finalI], finalI);
            });
        }
    }

    private void updateJobFieldsVisibility(Button currentButton, int rawButtonIndex) {
        int buttonIndex = rawButtonIndex + 1;

        setButtonVisibilityAndEnable(jobStartButtons[buttonIndex], true);
        setButtonVisibilityAndEnable(jobFinishButtons[buttonIndex], true);
        setButtonVisibilityAndEnable(currentButton, false);
        jobLabels[buttonIndex].setVisibility(View.VISIBLE);
        if (!currentButton.equals(addJobButtons[addJobButtons.length - 1]))
            setButtonVisibilityAndEnable(addJobButtons[buttonIndex], true);
    }

    private void setButtonVisibilityAndEnable(Button button, boolean isVisibleAndEnabled) {
        button.setVisibility(isVisibleAndEnabled ? View.VISIBLE : View.INVISIBLE);
        button.setEnabled(isVisibleAndEnabled);
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

            Pair<Integer, String> metaDataPair = getMetaDataFromActiveButton();
            updateButtonText(activeButton, formattedDateTime, metaDataPair.getSecond().equals("start") ? R.string.TC_start_button : R.string.TC_finish_button);
            for (DBRecordBuilder b : builders) {
                if (b.respondForId(metaDataPair.getFirst()))
                    b.add(dbHelper, metaDataPair.getFirst(), formattedDateTime, metaDataPair.getSecond().equals("start"));
            }
        }
    }

    private Pair<Integer, String> getMetaDataFromActiveButton() {
        String sourceName = activeButton.getResources().getResourceName(activeButton.getId());
        String[] tempArray = sourceName.split("_");
        return new Pair<>(Integer.parseInt(tempArray[1]), tempArray[2]); //id кнопки всегда имеет формат job_ID_start/finish_button,
        // следовательно в итоговом архиве после разбиения строки нам нужны 1 и 2 индексы как искомые ID и назначение.
    }

    @Override
    public void onBuilderFilled() {
        updateRestTimeText();
    }

    private void updateRestTimeText() {
        Pair<String, String>[] restTimes = timeCalculator.getRestTime();

        for (int i = 0; i < restTimes.length; i++){
            restTexts[i].setText("Отдых в ПО " + (i + 1) + "\nПолный отдых:\n" + restTimes[i].getFirst() + "\nКороткий отдых:\n" + restTimes[i].getSecond());
        }

        homeRestText.setText("Время полного\nдомашнего отдыха:\n" + timeCalculator.getHomeRestTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (DBRecordBuilder builder : builders) {
            builder.removeObserver(this);
        }
    }
}