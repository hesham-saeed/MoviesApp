package com.example.echo.moviesapp.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.Data.MoviesContract;

public class MovieTasks {


    public static final String ACTION_ADD_MOVIE_TO_FAVORITES = "add-movie-to-favorites";
    public static final String ACTION_DELETE_MOVIE_FROM_FAVOURTIES = "delete-movie-from-favorites";
    public static void executeTasks(final Context context, Intent intent,
                                    String action, Handler mHandler){

        if (action.equals(ACTION_ADD_MOVIE_TO_FAVORITES)){
            //Adding movie to SQLite Database
            final MovieItem movieItem;
            if (intent.hasExtra(PopularMoviesIntentService.EXTRA_MOVIE_ITEM)) {
                movieItem = intent.getParcelableExtra(PopularMoviesIntentService.EXTRA_MOVIE_ITEM);

                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.Movie.COLUMN_TITLE, movieItem.getTitle());
                cv.put(MoviesContract.Movie.COLUMN_PLOT,movieItem.getPlot() );
                cv.put(MoviesContract.Movie.COLUMN_POSTER_URI,context.getFilesDir()
                        + "/"+ movieItem.getPosterFilename());
                cv.put(MoviesContract.Movie.COLUMN_RATING, movieItem.getRating());
                cv.put(MoviesContract.Movie.COLUMN_DURATION, movieItem.getDuration());
                cv.put(MoviesContract.Movie.COLUMN_RELEASE_DATE, movieItem.getReleaseDate());

                context.getContentResolver().insert(MoviesContract.Movie.CONTENT_URI, cv);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}
