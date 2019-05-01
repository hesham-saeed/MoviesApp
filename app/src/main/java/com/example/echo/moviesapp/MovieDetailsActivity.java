package com.example.echo.moviesapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.Loaders.DurationLoader;
import com.example.echo.moviesapp.Loaders.ReviewsLoader;
import com.example.echo.moviesapp.Loaders.VideosLoader;
import com.example.echo.moviesapp.Utils.MoviesFetcher;
import com.example.echo.moviesapp.sync.MovieTasks;
import com.example.echo.moviesapp.sync.PopularMoviesIntentService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final String EXTRA_MOVIE_ID = "com.example.echo.moviesapp.movie_id";
    private static final String EXTRA_MOVIE_ITEM = "com.example.echo.moviesapp.movieitem";
    public static final String ARG_MOVIE_ID = "movie_id";

    //Loader IDs
    private static final int REVIEWS_LOADER_ID = 22;
    private static final int VIDEOS_LOADER_ID = 23;
    private static final int DURATION_LOADER_ID = 24;

    //Reference to the specific Movie
    private MovieItem movieItem;

    @BindView(R.id.tv_duration)    TextView durationTextView;
    @BindView(R.id.rv_trailers)    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.bt_favorite)
    FloatingActionButton addToFavorites;
    @BindView(R.id.rv_reviews)    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.iv_thumbnail)    ImageView posterImageView;
    @BindView(R.id.tv_title)    TextView titleTextView;
    @BindView(R.id.tv_release_year)    TextView releaseDateTextView;
    @BindView(R.id.tv_rating)    TextView ratingTextView;
    @BindView(R.id.tv_plot)    TextView plotTextView;
    @BindView(R.id.tv_empty_trailers)    TextView emptyTrailersTextView;
    @BindView(R.id.tv_empty_reviews)    TextView emptyReviewsTextView;


    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    //This function returns an intent to the calling class not to expose it's private fields
    //to achieve encapsulation for this class
    public static Intent newIntent(Context context, MovieItem movieItem) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE_ITEM, movieItem);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        //Checking if the intent contains the Parcelable MovieItem, to fill the layout properly
        // from the list of movies
        if (getIntent().hasExtra(EXTRA_MOVIE_ITEM)){

            //Fetching the proper MovieItem from the list of movies.
            movieItem = getIntent().getParcelableExtra(EXTRA_MOVIE_ITEM);

            //Filling the UI Elements of the activity with data
            titleTextView.setText(movieItem.getTitle());
            releaseDateTextView.setText(String.valueOf(movieItem.getReleaseDate()).substring(0, 4));
            plotTextView.setText(movieItem.getPlot());
            String rating = String.format(getString(R.string.rating_max), movieItem.getRating().toString());
            ratingTextView.setText(rating);

            String posterPath = movieItem.getPosterPath();
            String posterUrl = MoviesFetcher.POSTER_BASE_URL + posterPath;

            //Filling the ImageView with the Poster image using Picasso library which operates on
            // a background thread
            Picasso.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(posterImageView);
        }

        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMovieToFavorites();
            }
        });

        /*
        * Adding the MovieID to a bundle that will be used on 3 Endpoints: Reviews Endpoint,
        * Videos Endpoint, Duration Endpoint
        */
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MOVIE_ID, movieItem.getId());

        // Initializing loaders
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, bundle, this);
        getLoaderManager().initLoader(VIDEOS_LOADER_ID, bundle, this);
        getLoaderManager().initLoader(DURATION_LOADER_ID, bundle, this);

        //Initializing Trailers RecyclerView
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new TrailersAdapter();
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

        //Initializing Videos RecyclerView
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


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

    /*
     * This function retrieves the movie poster from ImageView and converts it to bitmap,
     * Then it compresses the image and Saves it to internal Storage as JPG file,
     * Finally, the poster location in internal storage is grouped together with other movie
     * information and an IntentService is used to Store these data in the offline SQLite Database
     * */
    private void addMovieToFavorites() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) posterImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        File file = new File(getFilesDir(), movieItem.getPosterFilename());
        if (!file.exists()) {
            FileOutputStream outputStream;
            try {

                outputStream = openFileOutput(file.getName(), MODE_PRIVATE);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                if (outputStream != null)
                    outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = PopularMoviesIntentService.newIntent(this,movieItem);
            intent.setAction(MovieTasks.ACTION_ADD_MOVIE_TO_FAVORITES);
            startService(intent);

        }
    }

    //An inner class for the trailers adapter
    class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

        List<String> mMovieVideos = new ArrayList<>();

        public void swap(List<String> movieVideos) {
            mMovieVideos = movieVideos;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TrailersViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trailer_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
            String trailerNumber = "Trailer " + String.valueOf(position+1);
            holder.trailerTextView.setText(trailerNumber);
            holder.trailerTextView.setTag(mMovieVideos.get(position));
        }

        @Override
        public int getItemCount() {
            return mMovieVideos.size();
        }

        class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.ib_play) ImageButton playButton;
            @BindView(R.id.tv_trailer) TextView trailerTextView;

            public TrailersViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                playButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/watch?v=";
                String youtubeKey = (String) trailerTextView.getTag();
                Uri uri = Uri.parse(url + youtubeKey);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        }
    }

    //An inner class for Reviews Adapter
    class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

        private List<Pair<String, String>> mMovieReviews = new ArrayList<>();

        @NonNull
        @Override
        public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewsViewHolder(getLayoutInflater()
                    .inflate(R.layout.review_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
            holder.authorTextView.setText(mMovieReviews.get(position).first);
            holder.reviewTextView.setText(mMovieReviews.get(position).second);
        }

        @Override
        public int getItemCount() {
            return mMovieReviews.size();
        }

        public void swapData(List<Pair<String, String>> movieReviews) {
            mMovieReviews = movieReviews;
            notifyDataSetChanged();
        }

        class ReviewsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_author) TextView authorTextView;
            @BindView(R.id.tv_review) TextView reviewTextView;

            public ReviewsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }



    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {

        //Launching loaders based on their unique ids
        switch (i) {
            case REVIEWS_LOADER_ID:
                return new ReviewsLoader(this, bundle);
            case VIDEOS_LOADER_ID:
                return new VideosLoader(this, bundle);
            case DURATION_LOADER_ID:
                return new DurationLoader(this, bundle);
            default:
                return null;
        }
    }

    /*
    * Methods to handle UI changes when a recyclerview is empty or when it's full
    */
    private void showEmptyTrailersMessage(){
        emptyTrailersTextView.setVisibility(View.VISIBLE);
    }
    private void hideEmptyTrailersMessage(){
        emptyTrailersTextView.setVisibility(View.INVISIBLE);
    }
    private void showEmptyReviewsMessage(){
        emptyReviewsTextView.setVisibility(View.VISIBLE);
    }
    private void hideEmptyReviewsMessage(){
        emptyReviewsTextView.setVisibility(View.INVISIBLE);
    }
    private void hideDuration(){
        durationTextView.setVisibility(View.INVISIBLE);
        durationTextView.setPadding(0,0,0,0);
    }

    @Override
    public void onLoadFinished(Loader loader, Object results) {

        //results are populated properly in the Layout based on the Loader ID
        switch (loader.getId()) {
            case REVIEWS_LOADER_ID:
                List<Pair<String, String>> movieReviews;
                try {
                     movieReviews = (List<Pair<String, String>>) results;
                     mReviewsAdapter.swapData(movieReviews);
                     if (mReviewsAdapter.getItemCount() == 0)
                         showEmptyReviewsMessage();
                     else
                         hideEmptyReviewsMessage();
                }catch(Exception e){
                    e.printStackTrace();
                    return;
                }

                break;
            case VIDEOS_LOADER_ID:
                List<String> movieVideos;
                try{
                    movieVideos = (List<String>) results;
                    mTrailersAdapter.swap(movieVideos);
                    if (mTrailersAdapter.getItemCount()>0)
                        hideEmptyTrailersMessage();
                    else
                        showEmptyTrailersMessage();
                }catch(Exception e){
                    e.printStackTrace();
                }

                break;
            case DURATION_LOADER_ID:
                String duration = (String) results;
                if (duration.equals("0"))
                {
                    hideDuration();
                }
                else {
                    movieItem.setDuration(Integer.parseInt(duration));
                    duration += "min";
                    durationTextView.setText(duration);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}
}
