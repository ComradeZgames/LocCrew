package ru.comradez.loccrew;

import androidx.annotation.NonNull;

public enum DataBaseNameList {
    BUFFER("BUFFER.db"),
    HISTORY("HISTORY.db");

    private final String value;

    DataBaseNameList(String s) {
        value = s;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}

