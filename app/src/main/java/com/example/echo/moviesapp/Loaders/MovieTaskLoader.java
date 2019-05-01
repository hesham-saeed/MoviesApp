package com.example.echo.moviesapp.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.MoviesListActivity;
import com.example.echo.moviesapp.Utils.MoviesFetcher;
import com.example.echo.moviesapp.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class MovieTaskLoader extends AsyncTaskLoader<List<MovieItem>> {

    private final Bundle mBundle;
    public MovieTaskLoader(Context context, Bundle bundle){
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
    public List<MovieItem> loadInBackground() {
        //Sending a Request to the theMoviedb Endpoint to fetch list of popular/recent movies
        //then parsing the response to objects to be used in Recycler View Adapters
        String path = mBundle.getString(MoviesListActivity.ARG_SORT);
        URL url = MoviesFetcher.buildUri(path);
        try {
            String jsonString = NetworkUtils.getResponse(url);
            return MoviesFetcher.parseJson(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
