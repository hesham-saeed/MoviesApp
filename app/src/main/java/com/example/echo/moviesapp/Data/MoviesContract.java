package com.example.echo.moviesapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MoviesContract {

    private MoviesContract(){}

    public static final String AUTHORITY = "com.example.echo.moviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class Movie implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URI = "poster";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_EXTRA = "extra";

    }

}
