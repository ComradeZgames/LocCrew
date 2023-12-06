package ru.comradez.loccrew;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

import kotlin.Pair;

public class TimeCalculator {

    DBHelper dbHelper;
    LinkedList<DateTimeString> datesTimes;
    int count;

    public TimeCalculator(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        datesTimes = new LinkedList<>(dbHelper.getAll());
        count = datesTimes.size();
    }

    private Pair<String, String> calculateRestFromSingleRecord(DateTimeString record) {
        String fullRest, halfRest;
        Duration fullDuration, halfDuration;
        int offset;

        fullDuration = Duration.between(LocalDateTime.parse(record.getStart(), DateTimeString.formatter), LocalDateTime.parse(record.getFinish(),
                DateTimeString.formatter));
        fullRest =  LocalDateTime.parse(record.getFinish(), DateTimeString.formatter).plus(fullDuration).format(DateTimeString.formatter);
        halfDuration = Duration.ofSeconds(fullDuration.getSeconds() / 2);
        if ((halfDuration.getSeconds() % 60) != 0)
            offset = 1;
        else offset = 0;
        halfRest = LocalDateTime.parse(record.getFinish(), DateTimeString.formatter).plus(halfDuration.plusMinutes(offset)).format(DateTimeString.formatter);

        return new Pair<String, String>(fullRest, halfRest);
    }
}
