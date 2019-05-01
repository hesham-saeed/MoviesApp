package com.example.echo.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.echo.moviesapp.Data.MovieItem;
import com.example.echo.moviesapp.Utils.MoviesFetcher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private List<MovieItem> movieItems = new ArrayList<>();
    private final Context context;
    public MovieAdapter(Context context){
        this.context = context;
        mListener = (OnClickListener) context;
    }
    private final OnClickListener mListener;
    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating each grid item
        return new MovieHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

        String posterPath = movieItems.get(position).getPosterPath();

        //using the tag element of the itemView to store the movie ID
        holder.itemView.setTag(position);

        //Retrieving the proper Movie Poster by it's URL
        String posterUrl = MoviesFetcher.POSTER_BASE_URL + posterPath;
        Picasso.with(context).
                load(posterUrl).
                placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder).
                into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return movieItems.size();
    }

    public interface OnClickListener{
        void onClick(MovieItem movieItem);
    }

    public void swapData(List<MovieItem> movieItems){
        if (movieItems == this.movieItems)
            return;

        //Checking for null data
        if (movieItems != null){
            this.movieItems = movieItems;
            notifyDataSetChanged();
        }
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView moviePoster;
        public MovieHolder(View itemView)
        {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.iv_movie_poster);
            moviePoster.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            //retrieving movie id from tag element of the itemView
            int position = (int) itemView.getTag();
            mListener.onClick(movieItems.get(position));
        }
    }
}
