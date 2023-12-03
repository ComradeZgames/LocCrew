package ru.comradez.loccrew;


import java.util.Date;

public class DateTimeString {
    long start;
    long finish;
    int id;

    public DateTimeString() {
        start = 0;
        finish = 0;
        id = 0;
    }
    public DateTimeString(int id, long time, boolean isStart) {
        if (isStart) {
            start = time;
            finish = 0;
        } else {
            start = 0;
            finish = time;
        }
        this.id = id;
    }


    public DateTimeString(int id,long start, long finish) {
        this.start = start;
        this.finish = finish;
        this.id = id;
    }

    public long getStart() { return start;}

    public long getFinish() { return finish; }

    public int getId() { return id; }

    public void setStart(long start) {
        this.start = start;
    }

    public void setFinish(long finish) {
        if ((finish - start) > 0) {
            this.finish = finish;
        }
    }

    public void setId(int id){
        this.id = id;
    }

}
