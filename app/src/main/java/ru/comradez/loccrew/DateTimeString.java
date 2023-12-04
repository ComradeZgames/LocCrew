package ru.comradez.loccrew;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeString {
    String start;
    String finish;
    int id;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy\nHH:mm");

    public DateTimeString() {
        start = null;
        finish = null;
        id = 0;
    }
    public DateTimeString(int id, String time, boolean isStart) {
        if (isStart) {
            start = time;
            finish = null;
        } else {
            start = null;
            finish = time;
        }
        this.id = id;
    }


    public DateTimeString(int id,String start, String finish) {
        this.start = start;
        this.finish = finish;
        this.id = id;
    }

    public String getStart() { return start;}

    public String getFinish() { return finish; }

    public int getId() { return id; }

    public void setStart(String start) {
        if (!LocalDateTime.parse(finish).equals(null)) {
            if (LocalDateTime.parse(start, formatter).isBefore(LocalDateTime.parse(finish)))
                this.start = start;
            else {
                finish = null;
                this.start = start;
            }
        }
    }

    public void setFinish(String finish) {
        if (LocalDateTime.parse(finish, formatter).isAfter(LocalDateTime.parse(start, formatter))) {
            this.finish = finish;
        }
    }

    public void setId(int id){
        this.id = id;
    }

}
