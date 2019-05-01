package com.example.echo.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.echo.moviesapp.Data.MovieItem;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_favorite_details_poster)
    ImageView posterImageView;
    @BindView(R.id.tv_favorite_details_duration)
    TextView durationTextView;
    @BindView(R.id.tv_favorite_details_rating)
    TextView ratingTextView;
    @BindView(R.id.tv_favorite_details_plot)
    TextView plotTextView;
    @BindView(R.id.tv_favorite_details_year)
    TextView yearTextView;

    private static final String EXTRA_MOVIE_ITEM = "com.example.echo.moviesapp.FavoriteMovieDetailsActivity.movie_item";

    //This function returns an intent to the calling class not to expose it's private fields
    //to achieve encapsulation for this class
    public static Intent newIntent(Context context, MovieItem movieItem){
        Intent intent = new Intent(context, FavoriteMovieDetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE_ITEM, movieItem);
        return intent;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movie_details);
        ButterKnife.bind(this);


        //Loading all data in their proper fields
        if (getIntent().hasExtra(EXTRA_MOVIE_ITEM)){
            final MovieItem movieItem = getIntent().getParcelableExtra(EXTRA_MOVIE_ITEM);

            Picasso.with(this)
                    .load("file://"+movieItem.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(posterImageView);

            String duration = String.valueOf(movieItem.getDuration()) + "min";
            String rating = movieItem.getRating().toString() + "/10";
            String year = String.valueOf(movieItem.getReleaseDate());
            String plot = movieItem.getPlot();
            String title = movieItem.getTitle();
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);



            if (actionBar != null)
                actionBar.setTitle(title);

            durationTextView.setText(duration);
            ratingTextView.setText(rating);
            plotTextView.setText(plot);

            yearTextView.setText(year);

        }
    }
}
