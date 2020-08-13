package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainPage extends Activity {

    private Button searchButton;
    private EditText title;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.search );
        searchButton = findViewById( R.id.searchtitlebutton );
        title = findViewById( R.id.searchtitle );
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchMovie( title.getText().toString().trim() );
            }
        });
    }

    private void switchMovie( String title){
        Intent movieList = new Intent( this, ListViewActivity.class );
        movieList.putExtra( "title", title );
        startActivity( movieList );
    }

}
