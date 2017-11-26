package com.example.jade.boardgamebuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewGame extends BaseActivity
{

    TextView txtGameName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);

        // Instantiate the toolbar
        Toolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtGameName = findViewById(R.id.txtGameName);

        txtGameName.setText(getIntent().getStringExtra("Game Name"));
    }
}
