package com.example.echo.moviesapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("ALL")
public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY,MoviesContract.PATH_MOVIES,
                MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY,
                MoviesContract.PATH_MOVIES + "/#"
                , MOVIE_WITH_ID);

        return uriMatcher;
    }

    //DB Helper
    private MoviesDBOpenHelper moviesDBOpenHelperHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        moviesDBOpenHelperHelper = new MoviesDBOpenHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase db = moviesDBOpenHelperHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch(match){
            case MOVIES:
                retCursor = db.query(MoviesContract.Movie.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,s1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = moviesDBOpenHelperHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match){
            case MOVIES:
                long id = db.insert(MoviesContract.Movie.TABLE_NAME, null,contentValues);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into "+ uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = moviesDBOpenHelperHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(MoviesContract.Movie.TABLE_NAME,
                        "_id=?"
                        ,new String[]{id});
                break;
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.Movie.TABLE_NAME,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (rowsDeleted>0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = moviesDBOpenHelperHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsUpdated = db.update(
                        MoviesContract.Movie.TABLE_NAME,
                        contentValues,
                        "_id=?",
                        new String[]{id});
                break;
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.Movie.TABLE_NAME,
                        contentValues,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        if (rowsUpdated>0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}
