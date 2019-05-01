package com.example.echo.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.Data.MoviesContract;
import com.example.echo.moviesapp.sync.MovieTasks;
import com.example.echo.moviesapp.sync.PopularMoviesIntentService;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.rv_fav_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty_fav_movies)
    TextView mDefaultTextView;
    private FavoriteAdapter favoriteAdapter;

    private static final int MOVIE_LOADER_ID = 44;

    //Message for empty list of favorites
    private void showDefaultMessage() {
        mDefaultTextView.setVisibility(View.VISIBLE);
    }

    //Hiding Default message to show the list of movies
    private void hideDefaultMessage() {
        mDefaultTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        favoriteAdapter = new FavoriteAdapter(this);
        mRecyclerView.setAdapter(favoriteAdapter);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (isDeviceInLandscape(this))
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        else
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new MovieTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favoriteAdapter.swapCursor(data);
        if (favoriteAdapter.getItemCount() == 0)
            showDefaultMessage();
        else
            hideDefaultMessage();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class MovieTaskLoader extends AsyncTaskLoader<Cursor> {

        Cursor mMoviesData = null;

        public MovieTaskLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (mMoviesData != null) {
                deliverResult(mMoviesData);
            } else {
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            try {
                return getContext().getContentResolver().query(MoviesContract.Movie.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            } catch (Exception e) {
                Log.e(getClass().getName(), "Failed to asynchronously load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor data) {
            mMoviesData = data;
            super.deliverResult(data);
        }
    }


    class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

        private final Context mContext;
        private Cursor mCursor;

        public FavoriteAdapter(Context context) {
            mContext = context;
        }

        @Override
        public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FavoriteViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.favorite_list_item, parent, false));
        }

        public void swapCursor(Cursor cursor) {

            if (cursor == mCursor)
                return;

            mCursor = cursor;
            if (mCursor != null)
                this.notifyDataSetChanged();

        }

        @Override
        public void onBindViewHolder(FavoriteViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            String id = mCursor.getString(mCursor.getColumnIndex(MoviesContract.Movie._ID));
            holder.itemView.setTag(R.string.id, id);
            holder.itemView.setTag(R.string.position, position);
            String title = mCursor.getString(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_TITLE));


            String uri = mCursor.getString(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_POSTER_URI));
            Log.d("Echo", uri);
            holder.itemView.setTag(R.string.location, uri);

            String year = String.valueOf
                    (mCursor.getInt(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_RELEASE_DATE)));
            //Toast.makeText(mContext,uri,Toast.LENGTH_SHORT).show();
            Picasso.with(mContext)
                    .load("file://" + uri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(holder.posterImageView);
            String titleYear = title + "(" + year + ")";
            holder.titleTextView.setText(titleYear);
        }

        @Override
        public int getItemCount() {
            if (mCursor == null)
                return 0;
            return mCursor.getCount();
        }

        class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.iv_fav_movie)
            ImageView posterImageView;
            @BindView(R.id.tv_fav_title)
            TextView titleTextView;

            public FavoriteViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                //Creating a new intent and running Details Activity for Favorite
                int position = (int) itemView.getTag(R.string.position);

                mCursor.moveToPosition(position);
                String id = mCursor.getString(mCursor.getColumnIndex(MoviesContract.Movie._ID));
                String title = mCursor
                        .getString(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_TITLE));
                String plot = mCursor
                        .getString(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_PLOT));
                String posterUri = mCursor.
                        getString(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_POSTER_URI));
                double rating = mCursor.
                        getDouble(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_RATING));
                int duration = mCursor.
                        getInt(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_DURATION));
                int releaseDate = mCursor.
                        getInt(mCursor.getColumnIndex(MoviesContract.Movie.COLUMN_RELEASE_DATE));
                MovieItem movieItem =
                        new MovieItem(id, posterUri, title, duration, plot, releaseDate, rating);

                Intent intent = FavoriteMovieDetailsActivity
                        .newIntent(FavoriteMoviesActivity.this, movieItem);
                startActivity(intent);

            }
        }
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
