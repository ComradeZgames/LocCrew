package ru.comradez.loccrew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "TIME_CALC";
    private static final String COLUMN_START = "START";
    private static final String COLUMN_FINISH = "FINISH";
    private static final String COLUMN_ID = "ID";

    public DBHelper(@Nullable Context context) {
        super(context, "BUFFER.sql", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER" + COLUMN_START + " DATETIME, " + COLUMN_FINISH + " DATETIME);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void AddRecord(DateTimeString dateTimeString) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, dateTimeString.getId());
        contentValues.put(COLUMN_START, dateTimeString.getStart());
        contentValues.put(COLUMN_FINISH, dateTimeString.getFinish());
        db.insert(TABLE_NAME, null, contentValues);

        db.close();
    }

    public LinkedList<DateTimeString> GetAll() {
        LinkedList<DateTimeString> resultList = new LinkedList<DateTimeString>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id_start = cursor.getColumnIndex(COLUMN_START);
                int id_finish = cursor.getColumnIndex(COLUMN_FINISH);
                int id_id = cursor.getColumnIndex(COLUMN_ID);
                DateTimeString dateTimeString = new DateTimeString(cursor.getInt(id_id), cursor.getLong(id_start), cursor.getLong(id_finish));
                resultList.add(dateTimeString);
            } while (cursor.moveToNext());
        }
        db.close();
        return resultList;
    }
}

