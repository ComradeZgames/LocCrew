package ru.comradez.loccrew;


import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DBRecordBuilder {

    private final int id;
    private String start = null, finish = null;
    private final List<BuilderObserver> observers = new ArrayList<>();
    private final Context context;

    public DBRecordBuilder(Context context,int id) {
        this.id = id;
        this.context = context;
    }

    public void addObserver(BuilderObserver observer) {
        observers.add(observer);
    }
    public void removeObserver(BuilderObserver observer) {
        observers.remove(observer);
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }

    private void notifyObservers() {
        for (BuilderObserver observer : observers) {
            observer.onBuilderFilled(id - 1);
        }
    }

    public boolean load(DBHelper db) {
        LinkedList<DateTimeString> dbRecords = db.getAll();
        for (DateTimeString record : dbRecords) {
            if (respondForId(record.getId())) {
                this.start = record.getStart();
                this.finish = record.getFinish();
                if (isComplete())
                    notifyObservers();
                return true;
            }
        }
        return false;
    }

    public boolean add(DBHelper db, int checkedId, String time, boolean isStart) {
        if (checkedId == id) {
            if (isStart){
                if (finish != null && (LocalDateTime.parse(time, DateTimeString.formatter).isAfter(LocalDateTime.parse(finish, DateTimeString.formatter)))) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle(R.string.TC_dialog_title_invalid);
                    dialogBuilder.setMessage(R.string.TC_dialog_message_invalid_start);
                    AlertDialog warningDialog = dialogBuilder.create();
                    warningDialog.show();
                    return false;
                }
                else start = time;
            }
            else {
                 if (LocalDateTime.parse(time, DateTimeString.formatter).isBefore(LocalDateTime.parse(start, DateTimeString.formatter))){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle(R.string.TC_dialog_title_invalid);
                    dialogBuilder.setMessage(R.string.TC_dialog_message_invalid_finish);
                    AlertDialog warningDialog = dialogBuilder.create();
                    warningDialog.show();
                    return false;
                } else finish = time;
            }
        }
        if (isComplete()) {
            if (db.isContains(checkedId))
                db.updateRecord(returnResult());
            else db.AddRecord(returnResult());

            notifyObservers();
        } return true;
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

