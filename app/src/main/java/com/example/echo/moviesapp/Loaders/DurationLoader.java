package com.example.echo.moviesapp.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.echo.moviesapp.MovieDetailsActivity;
import com.example.echo.moviesapp.Utils.MoviesFetcher;
import com.example.echo.moviesapp.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class DurationLoader extends AsyncTaskLoader<String>{

    private final Bundle mBundle;
    public DurationLoader(Context context, Bundle bundle){
        super(context);
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        if (mBundle == null)
            return;
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        //Sending a Request to the Duration Endpoint
        //then parsing the response to objects to be used
        String movieID = mBundle.getString(MovieDetailsActivity.ARG_MOVIE_ID);
        URL url = MoviesFetcher.buildExtraInfoUri(movieID);
        try {
            String response = NetworkUtils.getResponse(url);
            Log.d(getClass().getName(), response);
            return MoviesFetcher.parseDuration(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
