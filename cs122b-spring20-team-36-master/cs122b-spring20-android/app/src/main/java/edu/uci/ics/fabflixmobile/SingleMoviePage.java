package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import java.util.ArrayList;

public class SingleMoviePage extends Activity {

    private TextView title;
    private TextView year;
    private TextView director;
    private TextView rating;
    private TextView stars;
    private TextView genres;
    private Button backButton;
    private String id;
    private String url = "https://ec2-3-133-100-66.us-east-2.compute.amazonaws.com:8443/cs122b-spring20-project1-api-example/api/";

    private String searchTitle;
    private int pageNum;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        Log.d("single-movie", "Testing...");
        super.onCreate( savedInstanceState );
        setContentView( R.layout.singlemovie );
        title = findViewById( R.id.singletitle );
        year = findViewById( R.id.singleyear );
        director = findViewById( R.id.singledirector );
        rating = findViewById( R.id.singlerating );
        stars = findViewById( R.id.singlestars );
        genres = findViewById( R.id.singlegenres );
        backButton = findViewById( R.id.singleback );
        id = getIntent().getStringExtra("movieId" );
        searchTitle = getIntent().getStringExtra( "title" );
        pageNum = getIntent().getIntExtra( "pageNum", 0 );
        populateSingleMovie();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent movieList = new Intent( getBaseContext(), ListViewActivity.class );
                movieList.putExtra("pageNum", pageNum );
                movieList.putExtra("title", searchTitle );
                startActivity( movieList );
            }
        });
    }

    public void populateSingleMovie(){

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Movie m = new Gson().fromJson( response, Movie.class );
                title.setText( m.getTitle() );
                year.setText( "Year: " + m.getYear() );
                director.setText( "Director: " + m.getDirector() );
                rating.setText( "Rating: " + m.getRating() );

                ArrayList<Genre> g = m.getGenres();
                ArrayList<Star> s = m.getStars();
                String temp = "";
                for( int i = 0; i < g.size(); ++i ){
                    temp += g.get(i).getName() + " ";
                }
                genres.setText( "Genre(s): " + temp );
                temp = "";
                for( int i = 0; i < s.size(); ++i ){
                    temp += s.get(i).getName();
                    if( i != s.size() - 1 ){
                        temp += ", ";
                    }
                }
                stars.setText( "Star(s): " + temp );
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                });
        queue.add( movieRequest );
    }
}
