package com.example.echo.moviesapp.Data;


import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements Parcelable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private String id;
    private String posterPath;
    private String title;
    private int duration;
    private String plot;
    private int releaseDate;
    private Double rating;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(int releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }


    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPosterFilename(){
        return "IMG_" + getId() + ".JPG";
    }

    public MovieItem(String id, String posterPath, String title, int duration, String plot, int releaseDate, Double rating) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.duration = duration;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public MovieItem(){}

    public MovieItem(Parcel in){
        this.id = in.readString();
        this.posterPath = in.readString();
        this.title = in.readString();
        this.duration = in.readInt();
        this.plot = in.readString();
        this.releaseDate = in.readInt();
        this.rating = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.posterPath);
        dest.writeString(this.title);
        dest.writeInt(this.duration);
        dest.writeString(this.plot);
        dest.writeInt(this.releaseDate);
        dest.writeDouble(this.rating);
    }
}









