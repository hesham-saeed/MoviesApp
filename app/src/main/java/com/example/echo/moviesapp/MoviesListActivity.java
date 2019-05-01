package com.example.echo.moviesapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.Loaders.MovieTaskLoader;
import com.example.echo.moviesapp.Utils.MoviesFetcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<MovieItem>>, MovieAdapter.OnClickListener{

    public static final String ARG_SORT = "sort";
    @BindView(R.id.rv_movies) RecyclerView moviesRecyclerView;
    private MovieAdapter movieAdapter;

    private static final int MOVIES_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        movieAdapter = new MovieAdapter(this);
        moviesRecyclerView.setAdapter(movieAdapter);

        //Configuring the RecyclerView layout as Grid of 2 columns
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //Adding the sort type to the bundle
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SORT, MoviesFetcher.PATH_POPULAR);

        getLoaderManager().initLoader(MOVIES_LOADER_ID,bundle,this);
        if (isDeviceInLandscape(this))
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        else
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_sort:
                Bundle bundle = new Bundle();

                //Sorting Movies List by Popular or Top rated
                if (item.getTitle().equals(getString(R.string.title_top_rated))){
                    item.setTitle(getString(R.string.title_popular));
                    getSupportActionBar().setTitle(getString(R.string.title_top_rated));
                    bundle.putString(ARG_SORT,MoviesFetcher.PATH_TOP_RATED);
                } else if (item.getTitle().equals(getString(R.string.title_popular))){
                    bundle.putString(ARG_SORT,MoviesFetcher.PATH_POPULAR);
                    getSupportActionBar().setTitle(getString(R.string.title_popular));
                    item.setTitle(getString(R.string.title_top_rated));
                }

                //Restarting the loader to re-fetch the proper data
                getLoaderManager().restartLoader(MOVIES_LOADER_ID,bundle, this);

                return true;
            case R.id.action_favorites_activity:
                Intent intent =
                        new Intent(MoviesListActivity.this, FavoriteMoviesActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<MovieItem>> onCreateLoader(int i,final Bundle bundle) {
        return new MovieTaskLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieItem>> loader, List<MovieItem> movieItems) {
        //swapping the old data with new one
        movieAdapter.swapData(movieItems);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieItem>> loader) {

    }

    //Implementing the onClick of the ViewHolder in MovieAdapter class
    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = MovieDetailsActivity.newIntent(this, movieItem);
        startActivity(intent);
    }

    public boolean isDeviceInLandscape(Context context){
        final int screenOrientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (screenOrientation) {
            case Surface.ROTATION_180:
            case Surface.ROTATION_0:
                return false;
            default:
                return true;
        }
    }
}
