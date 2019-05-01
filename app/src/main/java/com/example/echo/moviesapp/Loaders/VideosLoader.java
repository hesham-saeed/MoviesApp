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
import java.util.List;

public class VideosLoader extends AsyncTaskLoader<List<String>> {
    private final Bundle mBundle;
    public VideosLoader(Context context, Bundle bundle) {
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
    public List<String> loadInBackground() {
        //Sending a Request to the Videos Endpoint
        //then parsing the response t objects to be used in Recycler View Adapters
        List<String> videos = null;
        String movieID = mBundle.getString(MovieDetailsActivity.ARG_MOVIE_ID);

        URL url = MoviesFetcher.buildTrailerUri(movieID);

        try {
            String response = NetworkUtils.getResponse(url);
            videos = MoviesFetcher.parseVideos(response);
            Log.v(VideosLoader.class.getName(), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videos;
    }
}
