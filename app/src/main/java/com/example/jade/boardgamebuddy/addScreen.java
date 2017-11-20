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

/*
 *
 * Accidentally named this activity using lower camel case. It will haunt me forever.
 *
 */
public class addScreen extends BaseActivity
{
    Button btnAddPlayer;
    Button btnAddGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_screen);

        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnAddPlayer = (Button)findViewById(R.id.btnAddPlayer);
        btnAddGame = (Button)findViewById(R.id.btnAddGame);

        btnAddPlayer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(addScreen.this, AddPlayer.class);

                startActivityForResult(intent, 0);
            }
        });

        btnAddGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(addScreen.this, AddGame.class);

                startActivityForResult(intent, 0);
            }
        });
    }

}
