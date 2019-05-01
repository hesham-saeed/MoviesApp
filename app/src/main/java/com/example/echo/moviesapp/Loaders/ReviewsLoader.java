package com.example.echo.moviesapp.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.example.echo.moviesapp.MovieDetailsActivity;
import com.example.echo.moviesapp.Utils.MoviesFetcher;
import com.example.echo.moviesapp.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ReviewsLoader extends AsyncTaskLoader<List<Pair<String, String>>> {

    private final Bundle mBundle;
    public ReviewsLoader(Context context, Bundle bundle) {
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
    public List<Pair<String, String>> loadInBackground() {
        //Sending a Request to the Reviews Endpoint
        //then parsing the response t objects to be used in Recycler View Adapters
        String id = mBundle.getString(MovieDetailsActivity.ARG_MOVIE_ID);
        URL url = MoviesFetcher.buildReviewsUri(id);
        try {
            String response = NetworkUtils.getResponse(url);
            Log.d(ReviewsLoader.class.getName(), response);
            return MoviesFetcher.parseReviews(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
