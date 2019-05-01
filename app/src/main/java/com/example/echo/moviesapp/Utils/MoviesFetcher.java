package com.example.echo.moviesapp.Utils;

import android.net.Uri;
import android.util.Pair;

import com.example.echo.moviesapp.BuildConfig;
import com.example.echo.moviesapp.Data.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MoviesFetcher {

    private MoviesFetcher(){

    }

    //Constant Strings for building theMovieDB URL.
    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String PARAM_API_KEY = "api_key";
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";

    //Building URL based on the sorting parameter
    public static URL buildUri(String theMovieDBPath){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(theMovieDBPath)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //Building Trailer URL
    public static URL buildTrailerUri(String movieID){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //Building Reviews URL.
    public static URL buildReviewsUri(String movieID){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //Building Duration URL.
    public static URL buildExtraInfoUri(String movieID){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //This function parses the jsonString retrieved into the proper MovieItem structure, fills a list of movies and returns it.
    public static List<MovieItem> parseJson(String jsonString) {
        List<MovieItem> movieItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonResultsArray = jsonObject.getJSONArray("results");
            for (int i=0;i<jsonResultsArray.length();i++){

                MovieItem movieItem = new MovieItem();

                JSONObject jsonMovieItem = jsonResultsArray.getJSONObject(i);

                String _id = jsonMovieItem.getString("id");
                String _rating = jsonMovieItem.getString("vote_average");
                String _posterPath = jsonMovieItem.getString("poster_path");
                String _plot = jsonMovieItem.getString("overview");
                String _title = jsonMovieItem.getString("original_title");
                String _releaseDate = jsonMovieItem.getString("release_date");

                movieItem.setId(_id);
                movieItem.setPlot(_plot);
                movieItem.setPosterPath(_posterPath);
                movieItem.setRating(Double.parseDouble(_rating));
                movieItem.setTitle(_title);
                movieItem.setReleaseDate(Integer.parseInt(_releaseDate.substring(0,4)));

                movieItems.add(movieItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieItems;
    }

    //This function parses the jsonString retrieved into a list of pairs, each pair contains the Reviewer name and Review content
    //This list is finally returned to the calling function.
    public static List<Pair<String,String>> parseReviews(String json){
        List<Pair<String,String>> reviews = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonResultsArray = jsonObject.getJSONArray("results");

            for (int i=0;i<jsonResultsArray.length();i++){
                JSONObject jsonReview = jsonResultsArray.getJSONObject(i);

                String author = jsonReview.getString("author");
                String content = jsonReview.getString("content");

                Pair<String,String> reviewPair = new Pair<>(author, content);
                reviews.add(reviewPair);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    //This function parses the jsonString retrieved into a list of Strings that carry videos URLs
    public static List<String> parseVideos(String json){
        List<String> videos = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            for (int i =0;i<jsonArrayResults.length(); i++){
                JSONObject jsonVideo = jsonArrayResults.getJSONObject(i);
                String youtubeKey = jsonVideo.getString("key");
                videos.add(youtubeKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videos;
    }

    //This function parses the jsonString and gets the Duration, checks if it's null and returning it to the caller.
    public static String parseDuration(String json){
        String duration = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            duration = jsonObject.getString("runtime");
            if (duration == null || duration.equals("null"))
                duration = "0";
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return duration;
    }

}
