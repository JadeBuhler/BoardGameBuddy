package com.example.jade.boardgamebuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity
{
    Button btnAdd;
    ListView lvPlayers;
    DatabaseHelper dbHelper;
    Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the toolbar
        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Instantiate the add button.
        btnAdd = (Button)findViewById(R.id.btnAdd);

        // Instantiate the players list view.
        lvPlayers = (ListView)findViewById(R.id.lvPlayers);

        btnTest = findViewById(R.id.btnTest);

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, addScreen.class);

                startActivityForResult(intent, 0);
            }
        });

        dbHelper = new DatabaseHelper(this);

        btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loadData();
            }
        });

        loadData();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadData();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    public void loadData()
    {
        // Instantiate the DataBaseHelper
        dbHelper = new DatabaseHelper(this);

        //load the data into a local variable
        ArrayAdapter<String> lstAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listview_item,
                dbHelper.loadData());

        //create a simple adapter
        //SimpleAdapter sa = new SimpleAdapter(this,lm,R.layout.listview_item,new String[]
            //{"Name"}, new int[] {R.id.text1, R.id.text2});

        //assign the adapter to the
        lvPlayers.setAdapter(lstAdapter);
    }

}

