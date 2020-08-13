package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);
        TextView directorView = view.findViewById( R.id.director );
        TextView ratingView = view.findViewById( R.id.rating );
        TextView genreView = view.findViewById( R.id.genres );
        TextView starView = view.findViewById( R.id.stars );

        titleView.setText( movie.getTitle() );
        subtitleView.setText( "Year: " + movie.getYear() );// need to cast the year to a string to set the label
        directorView.setText( "Director: " + movie.getDirector() );
        ratingView.setText( "Rating: " + movie.getRating() );

        ArrayList<Genre> g = movie.getGenres();
        ArrayList<Star> s = movie.getStars();
        String temp = "";
        for( int i = 0; i < g.size(); ++i ){
            temp += g.get(i).getName() + " ";
        }
        genreView.setText( "Genre(s): " + temp );
        temp = "";
        for( int i = 0; i < s.size(); ++i ){
            temp += s.get(i).getName();
            if( i != s.size() - 1 ){
                temp += ", ";
            }
        }
        starView.setText( "Star(s): " + temp );

        return view;
    }
}