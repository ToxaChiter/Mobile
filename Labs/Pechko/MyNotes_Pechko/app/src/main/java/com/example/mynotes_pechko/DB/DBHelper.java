package com.example.mynotes_pechko.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    private static final String DB_TABLE = "goods";
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DB_TABLE + "("
                + "id integer primary key autoincrement,"
                + "description text" + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Сносим строки (данные) в таблице
        db.delete(DB_TABLE, null, null);

        // Устанавливаем "0" в строке `seq` таблицы `sqlite_sequence`
        ContentValues cv = new ContentValues();
        cv.put("seq", 0);
        db.update("sqlite_sequence", cv, "name = ?", new String[]{DB_TABLE});
    }

}
