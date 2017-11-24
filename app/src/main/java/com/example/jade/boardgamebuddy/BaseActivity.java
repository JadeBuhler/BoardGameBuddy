package com.example.jade.boardgamebuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Instantiate the toolbar
        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.toolbar_options));
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the toolbar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                // Open settings activity
                Intent intent = new Intent(BaseActivity.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.about:
                // Open about activity
                break;
            case R.id.faq:
                // Open FAQ activity
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
