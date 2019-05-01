package com.example.echo.moviesapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.echo.moviesapp.Data.MoviesContract.Movie.*;

public class MoviesDBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int VERSION_CODE = 15;

    private static final String DATABASE_ALTER_TABLE = "ALTER TABLE " + TABLE_NAME
            + " ADD COLUMN " + MoviesContract.Movie.COLUMN_EXTRA + " TEXT";

    public MoviesDBOpenHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_POSTER_URI + " TEXT UNIQUE, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_PLOT + " TEXT, " +
                COLUMN_RATING + " REAL, " +
                COLUMN_RELEASE_DATE + " INTEGER, " +
                COLUMN_DURATION + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i < 15)
            sqLiteDatabase.execSQL(DATABASE_ALTER_TABLE);
    }
}
