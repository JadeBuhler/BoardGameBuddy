package com.example.jade.boardgamebuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 *  Accidentally named this activity using lower camel case. It will haunt me forever.
 */
public class addScreen extends BaseActivity
{
    Button btnAddPlayer;
    Button btnAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_screen);

        // Display the toolbar
        Toolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnAddPlayer = findViewById(R.id.btnAddPlayer);
        btnAddGame = findViewById(R.id.btnAddGame);

        btnAddPlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Send the user to the AddPlayer activity
                Intent intent = new Intent(addScreen.this, AddPlayer.class);
                startActivity(intent);
            }
        });

        btnAddGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Send the user to the AddGame activity
                Intent intent = new Intent(addScreen.this, AddGame.class);
                startActivity(intent);
            }
        });
    }

}
