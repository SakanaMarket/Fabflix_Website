package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class ListViewActivity extends Activity {

    private ListView listView;
    MovieListViewAdapter adapter;
    ArrayList<Movie> movies = new ArrayList();
    private int pageNum;
    private String searchTitle;
    private Button nextButton;
    private Button prevButton;
    private Button searchButton;
    private String url = "https://ec2-3-133-100-66.us-east-2.compute.amazonaws.com:8443/cs122b-spring20-project1-api-example/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        listView = findViewById(R.id.list);
        nextButton = findViewById( R.id.next );
        prevButton = findViewById( R.id.prev );
        searchButton = findViewById( R.id.search );

        searchTitle = getIntent().getStringExtra("title" );
        if( searchTitle == null ){
            searchTitle = "";
        }
        pageNum = getIntent().getIntExtra("pageNum", 0 );

        adapter = new MovieListViewAdapter(movies, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                //String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getTitle(), movie.getYear());
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                switchSingle( searchTitle, movie.getId() );
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum--;
                populateMovies();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum++;
                populateMovies();
            }
        });

        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ){
                switchSearch();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        populateMovies();
        Log.d( "movies", movies.toString() );
    }
    private void switchSearch(){
        Intent searchPage = new Intent( this, MainPage.class );
        startActivity( searchPage );
    }
    private void switchSingle( String searchTitle, String movieId ){
        Intent singleMoviePage = new Intent( this, SingleMoviePage.class );
        singleMoviePage.putExtra( "title", searchTitle );
        singleMoviePage.putExtra( "pageNum", pageNum );
        singleMoviePage.putExtra( "movieId", movieId );
        startActivity( singleMoviePage );
    }
    public void populateMovies( ){

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieRequest = new StringRequest(Request.Method.GET, url + "movies?page=" + pageNum + "&movie_title=" + searchTitle, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d( "movies", response );
                movies.clear();
                ArrayList<Movie> m = new Gson().fromJson(response, new TypeToken<ArrayList<Movie>>(){}.getType());
                for( int i = 0; i < m.size(); ++i ){
                    if( i != 20 ) {
                        movies.add(m.get(i));
                    }
                }
                adapter.notifyDataSetChanged();

                if( m.size() == 21 ){
                    nextButton.setVisibility( View.VISIBLE );
                } else {
                    nextButton.setVisibility( View.GONE );
                }

                if( pageNum == 0 ){
                    prevButton.setVisibility( View.GONE );
                } else {
                    prevButton.setVisibility( View.VISIBLE );
                }

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