package ru.comradez.loccrew;

import java.time.format.DateTimeFormatter;

public class DateTimeString {
    String start;
    String finish;
    int id;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy\nHH:mm");

    public DateTimeString(int id, String start, String finish) {
        this.start = start;
        this.finish = finish;
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }

    public int getId() {
        return id;
    }

}
