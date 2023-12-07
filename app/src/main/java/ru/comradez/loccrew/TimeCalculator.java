package ru.comradez.loccrew;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;

import kotlin.Pair;

public class TimeCalculator {

    DBHelper dbHelper;
    LinkedList<DateTimeString> datesTimes;

    public TimeCalculator(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Pair<String, String>[] getRestTime() {
        datesTimes = new LinkedList<>(dbHelper.getAll());
        Pair<String, String>[] result = new Pair[datesTimes.size()];
        if (Arrays.stream(result).count() == 3){
           result = Arrays.copyOf(result, result.length - 1);
        }

        for (int i = 0; i < result.length; i++){
            result[i] = calculateRestFromSingleRecord(datesTimes.get(i));
        }
        return result;
    }
    public String getWorkDuration(String start, String finish) {
        String result;
        Duration workDuration = Duration.between(LocalDateTime.parse(start, DateTimeString.formatter), LocalDateTime.parse(finish,
                DateTimeString.formatter));
        result = workDuration.toHours() + " ч " + workDuration.toMinutes() % 60 + " мин";
        return result;
    }

    public String getHomeRestTime() {
        // Сначала вычисляем общее время работы
        Duration totalWorkDuration = Duration.ZERO;
        for (DateTimeString record : datesTimes) {
            totalWorkDuration = totalWorkDuration.plus(Duration.between(
                    LocalDateTime.parse(record.getStart(), DateTimeString.formatter),
                    LocalDateTime.parse(record.getFinish(), DateTimeString.formatter)
            ));
        }

        // Теперь вычитаем времена отдыха
        Duration totalRestDuration = Duration.ZERO;
        for (int i = 0; i < datesTimes.size() - 1; i++) {
            LocalDateTime finishTime = LocalDateTime.parse(datesTimes.get(i).getFinish(), DateTimeString.formatter);
            LocalDateTime nextStartTime = LocalDateTime.parse(datesTimes.get(i + 1).getStart(), DateTimeString.formatter);
            totalRestDuration = totalRestDuration.plus(Duration.between(finishTime, nextStartTime));
        }

        // Умножаем общее время работы на 2.6 и вычитаем время отдыха
        Duration homeRestDuration = totalWorkDuration.multipliedBy(26).dividedBy(10).minus(totalRestDuration);

        // Форматируем результат
        String homeRest = LocalDateTime.parse(datesTimes.getLast().getFinish(), DateTimeString.formatter).plus(homeRestDuration).format(DateTimeString.formatter);

        return homeRest;
    }

    private Pair<String, String> calculateRestFromSingleRecord(DateTimeString record) {
        String fullRest, halfRest;
        Duration fullDuration, halfDuration;
        int offset;

        fullDuration = Duration.between(LocalDateTime.parse(record.getStart(), DateTimeString.formatter), LocalDateTime.parse(record.getFinish(),
                DateTimeString.formatter));
        fullRest = LocalDateTime.parse(record.getFinish(), DateTimeString.formatter).plus(fullDuration).format(DateTimeString.formatter);
        halfDuration = Duration.ofSeconds(fullDuration.getSeconds() / 2);
        if ((halfDuration.getSeconds() % 60) != 0)
            offset = 1;
        else offset = 0;
        halfRest = LocalDateTime.parse(record.getFinish(), DateTimeString.formatter).plus(halfDuration.plusMinutes(offset)).format(DateTimeString.formatter);

        return new Pair<>(fullRest, halfRest);
    }
}
