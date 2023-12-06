package ru.comradez.loccrew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {
    private static String tableName;
    private static final String COLUMN_START = "START";
    private static final String COLUMN_FINISH = "FINISH";
    private static final String COLUMN_ID = "ID";

    public DBHelper(@Nullable Context context, DataBaseNameList baseName, String tableName) {
        super(context, baseName.toString(), null, 1);
        DBHelper.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ( " + COLUMN_ID + " INTEGER, " + COLUMN_START + " DATETIME, " + COLUMN_FINISH + " DATETIME);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public void AddRecord(DateTimeString dateTimeString) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, dateTimeString.getId());
        contentValues.put(COLUMN_START, dateTimeString.getStart());
        contentValues.put(COLUMN_FINISH, dateTimeString.getFinish());
        db.insert(tableName, null, contentValues);

        //db.close();
    }

    public void updateRecord(DateTimeString newRecord) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_START, newRecord.getStart());
        contentValues.put(COLUMN_FINISH, newRecord.getFinish());

        db.update(tableName, contentValues, COLUMN_ID + "=" + newRecord.getId(), null);
    }

    public boolean isContains(int id) {

        LinkedList<DateTimeString> checkedList = this.getAll();
        for (DateTimeString string : checkedList) {
            if (string.getId() == id) return true;
        }
        return false;
    }

    public LinkedList<DateTimeString> getAll() {
        LinkedList<DateTimeString> resultList = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id_start = cursor.getColumnIndex(COLUMN_START);
                int id_finish = cursor.getColumnIndex(COLUMN_FINISH);
                int id_id = cursor.getColumnIndex(COLUMN_ID);
                DateTimeString dateTimeString = new DateTimeString(cursor.getInt(id_id), cursor.getString(id_start), cursor.getString(id_finish));
                resultList.add(dateTimeString);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resultList;
    }
}

