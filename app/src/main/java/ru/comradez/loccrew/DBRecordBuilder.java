package ru.comradez.loccrew;


import java.util.LinkedList;

public class DBRecordBuilder {

    private final int id;
    private String start = null, finish = null;

    public DBRecordBuilder(int id) {
        this.id = id;

    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }

    public boolean load(DBHelper db) {
        LinkedList<DateTimeString> dbRecords = db.getAll();
        for (DateTimeString record : dbRecords) {
            if (respondForId(record.getId())) {
                this.start = record.getStart();
                this.finish = record.getFinish();
                return true;
            }
        }
        return false;
    }

    public void add(DBHelper db, int checkedId, String time, boolean isStart) {
        if (checkedId == id) {
            if (isStart)
                start = time;
            else finish = time;
        }
        if (isComplete()) {
            if (db.isContains(checkedId))
                db.updateRecord(returnResult());
            else db.AddRecord(returnResult());
        }
    }

    public boolean respondForId(int id) {
        return this.id == id;
    }

    private boolean isComplete() {
        return (start != null) && (finish != null);
    }

    private DateTimeString returnResult() {
        return new DateTimeString(id, start, finish);
    }
}

