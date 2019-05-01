package com.example.echo.moviesapp.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.echo.moviesapp.Data.MovieItem;

public class PopularMoviesIntentService extends IntentService {

    public static final String EXTRA_MOVIE_ITEM =
            "com.example.echo.moviesapp.sync.PopularMoviesIntentService.movieItem";
    Handler mHandler;
    //This function returns an intent to the calling class not to expose it's private fields
    //to achieve encapsulation
    public static Intent newIntent(Context context, MovieItem movieItem){
        Intent intent = new Intent(context, PopularMoviesIntentService.class);
        intent.putExtra(EXTRA_MOVIE_ITEM, movieItem);
        return intent;
    }
    //An intent service to handle SQLite Database Operations
    public PopularMoviesIntentService(){super("PopularMoviesIntentService");}

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();

        MovieTasks.executeTasks(this,intent,action, mHandler);


    }
}
